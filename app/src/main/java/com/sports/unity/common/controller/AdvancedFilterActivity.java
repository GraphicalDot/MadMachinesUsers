package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.sports.unity.R;
import com.sports.unity.common.controller.fragment.AdvancedFilterFragment;
import com.sports.unity.common.model.FavouriteContentHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 12/28/2015.
 */
public class AdvancedFilterActivity extends CustomAppCompatActivity {
    private Toolbar toolbar;
    private Bundle bundle;
    private AdvancedFilterFragment advancedFilterFragment;
    public ArrayList<String> favList;
    private TextView titleText;
    private ArrayList<OnEditFilterListener> editFilterListener;
    private ImageView search, searchClose;
    private LinearLayout titleLayout, searchLayout;
    private EditText searchText;
    public boolean isSearchEdit;
    public String searchString;
    public boolean isFromNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_filter_activity);
        editFilterListener = new ArrayList<OnEditFilterListener>();
        favList = UserUtil.getFavouriteFilters();
        bundle = getIntent().getExtras();
        try {
            isFromNav = bundle.getBoolean(Constants.IS_FROM_NAV, false);
        } catch (NullPointerException booleanNull) {

        }
        setUpToolBar();
        if (savedInstanceState == null) {
            initFragment();

        }
        setUpNextClick();
        setUpDoneClick();
    }

    private void setUpDoneClick() {
        final Button done = (Button) findViewById(R.id.done);
        done.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
        if (!UserUtil.isFilterCompleted() || !isFromNav) {
            done.setVisibility(View.INVISIBLE);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDoneClick(done);
            }
        });
    }

    private void setUpNextClick() {

        Button next = (Button) findViewById(R.id.next);
        next.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
        if (UserUtil.isFilterCompleted()) {
            next.setVisibility(View.INVISIBLE);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNextClick();
            }
        });
    }

    private void handleDoneClick(Button edit) {
        /*if (!isEditMode) {
            edit.setText("Done");
            search.setVisibility(View.VISIBLE);
            isEditMode = true;
            performEdit();
        } else {
            if(!isFromNav) {
                isEditMode = false;
                isSearchEdit = false;
                search.setVisibility(View.INVISIBLE);
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    titleLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                }
                edit.setText("Edit");
                performEdit();
            }else{*/
        UserUtil.setFavouriteFilters(AdvancedFilterActivity.this, favList);
        setResult(RESULT_OK);
        finish();
           /* }

        }*/
    }

    private void handleNextClick() {
        closeSearch();
        Bundle b = advancedFilterFragment.getArguments();
        UserUtil.setFavouriteFilters(AdvancedFilterActivity.this, favList);
        if (UserUtil.isFilterCompleted()) {
            finish();
        } else if (b.get(Constants.SPORTS_FILTER_TYPE).equals(Constants.FILTER_TYPE_LEAGUE)) {
            UserUtil.setLeagueSelected(AdvancedFilterActivity.this, true);
            bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
            titleText.setText("Select your favourite teams");
            replaceFragment(bundle);
        } else if (b.get(Constants.SPORTS_FILTER_TYPE).equals(Constants.FILTER_TYPE_TEAM)) {
            UserUtil.setTeamSelected(AdvancedFilterActivity.this, true);
            bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_PLAYER);
            titleText.setText("Select your favourite player");
            replaceFragment(bundle);
        } else if (!UserUtil.isPlayerSelected()) {
            UserUtil.setPlayerSelected(AdvancedFilterActivity.this, true);
            UserUtil.setFilterCompleted(AdvancedFilterActivity.this, true);
            moveToNextActivity(MainActivity.class);
        }

    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleText.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        search = (ImageView) toolbar.findViewById(R.id.action_search);
        search.setVisibility(View.VISIBLE);
        titleLayout = (LinearLayout) toolbar.findViewById(R.id.title_layout);
        searchLayout = (LinearLayout) toolbar.findViewById(R.id.search_layout);
        searchText = (EditText) toolbar.findViewById(R.id.search_edit);
        searchClose = (ImageView) toolbar.findViewById(R.id.search_close);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FavouriteContentHandler.getInstance().isDisplay) {
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
        if(editFilterListener==null){
            editFilterListener=new ArrayList<>();
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
        if (UserUtil.isFilterCompleted()) {
            addFragment();
        } else /*if (!UserUtil.isLeagueSelected())*/ {
            bundle = new Bundle();
            if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
                titleText.setText("Select your favourite leagues");

            } else {
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
                titleText.setText("Select your favourite teams");
            }
            addFragment();
        }


    }

    public void closeSearch() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            titleLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            searchText.setText("");
            isSearchEdit = false;
            performEdit();
        }
    }

    private void addFragment() {
        editFilterListener = new ArrayList<>();
        FavouriteContentHandler.getInstance().resetListener();
        advancedFilterFragment = new AdvancedFilterFragment();
        advancedFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.filter_container, advancedFilterFragment, bundle.getString(Constants.SPORTS_FILTER_TYPE)).commit();
        titleText.setText("Select your favourite " + bundle.getString(Constants.SPORTS_FILTER_TYPE));

    }

    private void replaceFragment(Bundle bundle) {
        editFilterListener = new ArrayList<>();
        FavouriteContentHandler.getInstance().resetListener();

        advancedFilterFragment = new AdvancedFilterFragment();
        advancedFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, advancedFilterFragment, bundle.getString(Constants.SPORTS_FILTER_TYPE)).commit();
    }

    public interface OnEditFilterListener {
        public void onEdit(boolean b);
    }

    private void performEdit() {
        if (editFilterListener.size() > 0) {
            for (OnEditFilterListener e : editFilterListener) {
                e.onEdit(isSearchEdit);
            }
        } else {
            //nothing
        }
    }

    @Override
    public void onBackPressed() {
        Bundle b = advancedFilterFragment.getArguments();
        if (isSearchEdit) {
            closeSearch();
        } else if (UserUtil.isFilterCompleted()) {
            if (isFromNav) {
                setResult(RESULT_CANCELED);
            }
            finish();
        } else if (b.get(Constants.SPORTS_FILTER_TYPE).equals(Constants.FILTER_TYPE_PLAYER)) {
            UserUtil.setLeagueSelected(AdvancedFilterActivity.this, true);
            bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
            titleText.setText("Select your favourite teams");
            replaceFragment(bundle);
        } else if (b.get(Constants.SPORTS_FILTER_TYPE).equals(Constants.FILTER_TYPE_TEAM)) {
            if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
                editFilterListener.remove(editFilterListener.size() - 1);
                UserUtil.setTeamSelected(AdvancedFilterActivity.this, true);
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
                titleText.setText("Select your favourite leagues");
                replaceFragment(bundle);
            } else {
                moveToNextActivity(SelectSportsActivity.class);
            }
        } else if (b.get(Constants.SPORTS_FILTER_TYPE).equals(Constants.FILTER_TYPE_LEAGUE)) {
            moveToNextActivity(SelectSportsActivity.class);
        }

    }
}
