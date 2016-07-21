package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.controller.fragment.AdvancedFilterFragment;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FavouriteContentHandler;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.FirebaseUtil;

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
    private ArrayList<onSearchListener> searchRefreshListener;
    public ImageView search, searchClose;
    private ImageView refresh;
    private LinearLayout titleLayout, searchLayout;
    private EditText searchText;
    public boolean isSearchEdit;
    public String searchString;
    private boolean isResultRequired;
    public int fragmentNum = 0;
    public ViewPager pager;
    private boolean isSingleUse;
    private String sportsType;
    public boolean isFirstInstall = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_filter_activity);
        searchRefreshListener = new ArrayList<onSearchListener>();
        favList = FavouriteItemWrapper.getInstance(this).getFavList();
        isFirstInstall = !UserUtil.isFilterCompleted() && favList.size() == 0;
        sportsSelected = UserUtil.getSportsSelected();
        bundle = getIntent().getExtras();
        if (isFirstInstall) {
            FavouriteContentHandler.getInstance(AdvancedFilterActivity.this).clearContent();
        }
        try {
            isResultRequired = getIntent().getExtras().getBoolean(Constants.RESULT_REQUIRED, false);
            isSingleUse = getIntent().getExtras().getBoolean(Constants.RESULT_SINGLE_USE, false);
            sportsType = getIntent().getExtras().getString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
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
        if (isSingleUse) {
            skip.setVisibility(View.GONE);
        }
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNextAnalytics(FirebaseUtil.Event.SKIP_CLICK_EVENT);
                if (!UserUtil.isFilterCompleted()) {
                    FavouriteItemWrapper.getInstance(AdvancedFilterActivity.this).saveList(AdvancedFilterActivity.this, favList);
                    UserUtil.setFilterCompleted(AdvancedFilterActivity.this, true);
                } else {
                    FavouriteContentHandler.getInstance(AdvancedFilterActivity.this).invalidate(AdvancedFilterActivity.this);
                }
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
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNextClick();
            }
        });
    }

    public void setUpViewPager(ViewPager pager) {
        this.pager = pager;
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                closeSearch();
            }
        });
    }

    private void handleNextClick() {
        FavouriteItemWrapper.getInstance(this).saveList(this, favList);
        if (isSingleUse) {
            beforeExitingActivity();
            setResult(RESULT_OK, getIntent());
            finish();
        } else if (fragmentNum < sportsSelected.size()) {
            sendNextAnalytics(FirebaseUtil.Event.NEXT_CLICK_EVENT);
            if (pager.getCurrentItem() < pager.getAdapter().getCount() - 1) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            } else {
                bundle.putString(Constants.SPORTS_TYPE, sportsSelected.get(fragmentNum));
                replaceFragment(bundle);
                titleText.setText(CommonUtil.capitalize(sportsSelected.get(fragmentNum)));
                fragmentNum++;
            }
        } else if (fragmentNum == sportsSelected.size()) {
//            enableNotificationsWhenSettingUpFirstTime();
            sendNextAnalytics(FirebaseUtil.Event.NEXT_CLICK_EVENT);
            if (pager.getCurrentItem() < pager.getAdapter().getCount() - 1) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            } else {
                beforeExitingActivity();
                if (isResultRequired) {
                    setResult(RESULT_OK, getIntent());
                    finish();
                } else {
                    moveToNextActivity(MainActivity.class);
                }
            }
        }
        closeSearch();
    }

    private void sendNextAnalytics(String eventName) {
        //FIREBASE INTEGRATION
        {
            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, titleText.getText().toString());
            if (titleText.getText().toString().equalsIgnoreCase(Constants.GAME_KEY_CRICKET)) {
                if (pager.getCurrentItem() == 0) {
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName + "_" + "c" + "_" + FirebaseUtil.Param.TEAM);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, FirebaseUtil.Param.TEAM);
                } else if (pager.getCurrentItem() == 1) {
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName + "_" + "c" + "_" + FirebaseUtil.Param.PLAYER);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, FirebaseUtil.Param.PLAYER);
                }
            } else if (titleText.getText().toString().equalsIgnoreCase(Constants.GAME_KEY_FOOTBALL)) {
                if (pager.getCurrentItem() == 0) {
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName + "_" + "f" + "_" + FirebaseUtil.Param.LEAGUE);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, FirebaseUtil.Param.LEAGUE);
                } else if (pager.getCurrentItem() == 1) {
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName + "_" + "f" + "_" + FirebaseUtil.Param.TEAM);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, FirebaseUtil.Param.TEAM);
                } else if (pager.getCurrentItem() == 2) {
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName + "_" + "f" + "_" + FirebaseUtil.Param.PLAYER);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, FirebaseUtil.Param.PLAYER);
                }
            }
        }
    }

    private void beforeExitingActivity() {
        UserUtil.setFilterCompleted(AdvancedFilterActivity.this, true);
        ContactsHandler.getInstance().addCallToUpdateUserFavorites(getApplicationContext());
    }

    private void onBack() {
        if (isSearchEdit) {
            closeSearch();
        } else {
            if (pager.getCurrentItem() != 0) {
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            } else {
                FavouriteContentHandler.getInstance(AdvancedFilterActivity.this).invalidate(AdvancedFilterActivity.this);
                fragmentNum--;
                if (fragmentNum != 0) {
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
    }

//    private void enableNotificationsWhenSettingUpFirstTime() {
//
//        /**
//         *  When we are setting up the app first time notifications needs to be disabled
//         *  before entering the main page so its does not hinder the registration process
//         */
//
//        if (UserUtil.isFilterCompleted()) {
//            //do nothing
//        } else {
//            UserUtil.setNotificationAndSound(getApplicationContext(), true);
//            UserUtil.setNotificationPreviews(getApplicationContext(), true);
//            UserUtil.setConversationVibrate(getApplicationContext(), true);
//            UserUtil.setConversationTones(getApplicationContext(), true);
//            UserUtil.setNotificationLight(getApplicationContext(), true);
//        }
//    }


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
        refresh = (ImageView) toolbar.findViewById(R.id.action_refresh);
        refresh.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRefresh();
            }
        });
    }

    public void addSearchListener(onSearchListener listener) {
        if (searchRefreshListener == null) {
            searchRefreshListener = new ArrayList<>();
        }
        searchRefreshListener.add(listener);
    }

    public void removeSearchListener(onSearchListener listener) {
        searchRefreshListener.remove(listener);
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
        searchRefreshListener = new ArrayList<>();
        FavouriteContentHandler.getInstance(this).resetListener();
        advancedFilterFragment = new AdvancedFilterFragment();
        advancedFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.filter_container, advancedFilterFragment, bundle.getString(Constants.SPORTS_TYPE)).commit();
        if (!isSingleUse) {
            titleText.setText(CommonUtil.capitalize(sportsSelected.get(0)));
        } else {
            titleText.setText(CommonUtil.capitalize(sportsType));
        }

    }

    private void replaceFragment(Bundle bundle) {
        searchRefreshListener = new ArrayList<>();
        FavouriteContentHandler.getInstance(this).resetListener();
        advancedFilterFragment = new AdvancedFilterFragment();
        advancedFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, advancedFilterFragment, bundle.getString(Constants.SPORTS_TYPE)).commit();
    }

    public interface onSearchListener {
        public void onSearch(boolean isSearchInitiated, String searchString);

        public void onRefresh();
    }

    private void performEdit() {
        if (searchRefreshListener.size() > 0) {
            for (onSearchListener e : searchRefreshListener) {
                e.onSearch(isSearchEdit, searchString);
            }
        } else {
            //nothing
        }
    }

    private void performRefresh() {
        if (searchRefreshListener.size() > 0) {
            for (onSearchListener e : searchRefreshListener) {
                e.onRefresh();
            }
        } else {
            //nothing
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

}
