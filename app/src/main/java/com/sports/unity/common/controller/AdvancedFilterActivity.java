package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.fragment.AdvancedFilterFragment;
import com.sports.unity.common.model.FavouriteContentHandler;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mad on 12/28/2015.
 */
public class AdvancedFilterActivity extends CustomAppCompatActivity {
    private Toolbar toolbar;
    private Bundle bundle;
    private AdvancedFilterFragment advancedFilterFragment;
    public ArrayList<FavouriteItem> favList;
    private ArrayList<String> sportsSelected;
    private TextView titleText;
    private ImageView back;
    private ArrayList<OnEditFilterListener> editFilterListener;
    public ImageView search, searchClose;
    private LinearLayout titleLayout, searchLayout;
    private EditText searchText;
    public boolean isSearchEdit;
    public String searchString;
    public boolean isFromNav;
    private boolean isResultRequired;
    private int listSize = 0;
    int fragmentNum = 0;
    JSONObject jsonObject = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_filter_activity);
        editFilterListener = new ArrayList<OnEditFilterListener>();
        favList = FavouriteItemWrapper.getInstance(this).getFavList();
        sportsSelected = UserUtil.getSportsSelected();
        bundle = getIntent().getExtras();
        try {
            isFromNav = true;
            isResultRequired = getIntent().getExtras().getBoolean(Constants.RESULT_REQUIRED);
        } catch (NullPointerException booleanNull) {

        }
        setUpToolBar();
        if (savedInstanceState == null) {
            initFragment();

        }
        setUpNextClick();
        setUpSkipClick();
    }

    private void setUpSkipClick() {
        Button skip = (Button) findViewById(R.id.skip);
        skip.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, false));
        skip.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
        if (!UserUtil.isFilterCompleted() || isFromNav) {
            skip.setVisibility(View.VISIBLE);
        } else {
            skip.setVisibility(View.INVISIBLE);
        }
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUtil.setFilterCompleted(AdvancedFilterActivity.this, true);
                FavouriteContentHandler.getInstance(AdvancedFilterActivity.this).invalidate(AdvancedFilterActivity.this);
                if (isResultRequired) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    moveToNextActivity(MainActivity.class);
                }
            }
        });
    }

    private void setUpNextClick() {

        Button next = (Button) findViewById(R.id.next);
        next.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        next.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
        if (!UserUtil.isFilterCompleted() || isFromNav) {
            next.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.INVISIBLE);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNextClick();
            }
        });
    }

    private void handleNextClick() {
        FavouriteItemWrapper.getInstance(this).saveList(this, favList);

        if (UserUtil.isFilterCompleted() && !isFromNav) {
            finish();
        } else if (fragmentNum < sportsSelected.size()) {
            bundle.putString(Constants.SPORTS_TYPE, sportsSelected.get(fragmentNum));
            replaceFragment(bundle);
            titleText.setText(CommonUtil.capitalize(sportsSelected.get(fragmentNum)));
            fragmentNum++;
        } else if (fragmentNum == sportsSelected.size()) {
            UserUtil.setFilterCompleted(AdvancedFilterActivity.this, true);
            VcardThread updateVcard = new VcardThread();
            updateVcard.start();
            if (isResultRequired) {
                Log.d("max","setting result"+RESULT_OK);
                setResult(RESULT_OK, getIntent());
                finish();
            } else {
                moveToNextActivity(MainActivity.class);
            }
        }
        closeSearch();

    }


    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleText.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());
        back = (ImageView) toolbar.findViewById(R.id.img_back);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        search = (ImageView) toolbar.findViewById(R.id.action_search);
        search.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        search.setVisibility(View.VISIBLE);
        titleLayout = (LinearLayout) toolbar.findViewById(R.id.title_layout);
        searchLayout = (LinearLayout) toolbar.findViewById(R.id.search_layout);
        searchText = (EditText) toolbar.findViewById(R.id.search_edit);
        searchClose = (ImageView) toolbar.findViewById(R.id.search_close);
        searchClose.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        toolbar.bringToFront();
        findViewById(R.id.rootview).invalidate();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FavouriteContentHandler.getInstance(AdvancedFilterActivity.this).isDisplay) {
                    if (titleLayout.getVisibility() == View.VISIBLE) {
                        titleLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.VISIBLE);
                        isSearchEdit = true;
                        searchText.setFocusable(true);
                        searchText.requestFocus();
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
                    }
                } else {
                    Toast.makeText(AdvancedFilterActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    titleLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    isSearchEdit = false;
                    searchText.setText("");
                    performEdit();
                }
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    searchString = searchText.getText().toString();
                    if (!TextUtils.isEmpty(searchString)) {

                        isSearchEdit = true;
                        performEdit();
                    } else {
                        Toast.makeText(AdvancedFilterActivity.this, "Please enter your query", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void addEditClickListener(OnEditFilterListener listener) {
        if (editFilterListener == null) {
            editFilterListener = new ArrayList<>();
        }
        editFilterListener.add(listener);
    }

    public void removeEditClickListener(OnEditFilterListener listener) {
        editFilterListener.remove(listener);
    }

    private void moveToNextActivity(Class nextActivityClass) {
        Intent mainIntent = new Intent(AdvancedFilterActivity.this, nextActivityClass);
        startActivity(mainIntent);
        finish();
    }


    private void initFragment() {
        addFragment();
    }


    public void closeSearch() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            titleLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            searchText.setText("");
            isSearchEdit = false;
            performEdit();
        }
    }

    private void addFragment() {
        fragmentNum = 1;
        editFilterListener = new ArrayList<>();
        FavouriteContentHandler.getInstance(this).resetListener();
        advancedFilterFragment = new AdvancedFilterFragment();
        advancedFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.filter_container, advancedFilterFragment, bundle.getString(Constants.SPORTS_TYPE)).commit();
        titleText.setText(CommonUtil.capitalize(sportsSelected.get(0)));

    }

    private void replaceFragment(Bundle bundle) {
        editFilterListener = new ArrayList<>();
        FavouriteContentHandler.getInstance(this).resetListener();
        advancedFilterFragment = new AdvancedFilterFragment();
        advancedFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, advancedFilterFragment, bundle.getString(Constants.SPORTS_TYPE)).commit();
    }

    public interface OnEditFilterListener {
        public void onEdit(boolean b, String searhString);
    }

    private void performEdit() {
        if (editFilterListener.size() > 0) {
            for (OnEditFilterListener e : editFilterListener) {
                e.onEdit(isSearchEdit, searchString);
            }
        } else {
            //nothing
        }
    }

    private void onBack() {
        if (isSearchEdit) {
            closeSearch();
        } else {
            FavouriteContentHandler.getInstance(AdvancedFilterActivity.this).invalidate(AdvancedFilterActivity.this);
            fragmentNum--;
            if (UserUtil.isFilterCompleted() && !isFromNav) {
                if (isResultRequired) {
                    setResult(RESULT_CANCELED);
                }
                finish();
            } else if (fragmentNum != 0) {
                bundle.putString(Constants.SPORTS_TYPE, sportsSelected.get(fragmentNum - 1));
                replaceFragment(bundle);
                titleText.setText(CommonUtil.capitalize(UserUtil.getSportsSelected().get(fragmentNum - 1)));
            } else if (fragmentNum == 0) {
                if (!isResultRequired) {
                    moveToNextActivity(SelectSportsActivity.class);
                } else {
                    if (isResultRequired) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private class VcardThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                if (UserUtil.isFilterCompleted() && XMPPClient.getConnection() != null) {
                    VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
                    VCard vCard = new VCard();
                    vCard.load(XMPPClient.getConnection());
                    vCard.setField("fav_list", TinyDB.getInstance(AdvancedFilterActivity.this).getString(TinyDB.FAVOURITE_FILTERS));
                    manager.saveVCard(vCard);
                    UserUtil.setFavouriteVcardUpdated(AdvancedFilterActivity.this, true);

                } else {
                    UserUtil.setFavouriteVcardUpdated(AdvancedFilterActivity.this, false);
                }

            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
}
