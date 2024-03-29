package com.sports.unity.news.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.GlobalSearchActivity;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.news.model.NewsContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

public class NewsSearchActivity extends CustomAppCompatActivity {
    private Bundle searchFilterBundle;

    private static final String NEWS_FRAGMENT_TAG = "news_fragment_tag";

    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_search);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        automaticSearch();
        checkForGlobalSearchKeyword();
    }

    private void checkForGlobalSearchKeyword() {
        String keyword = getIntent().getStringExtra(GlobalSearchActivity.KEYWORD);
        if (keyword != null) {
            search.setText(keyword);
            performSearch(keyword);
            search.clearFocus();
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void automaticSearch() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_search);
        EditText search = (EditText) toolbar.findViewById(R.id.search_view);
        ImageView clear_search = (ImageView) toolbar.findViewById(R.id.search_clear);
        clear_search.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));
        checkForFilteredSearch(search, clear_search);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_search);
        search = (EditText) toolbar.findViewById(R.id.search_view);
        ImageView back = (ImageView) toolbar.findViewById(R.id.img_back);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));
        final ImageView clear_search = (ImageView) toolbar.findViewById(R.id.search_clear);
        clear_search.setVisibility(View.INVISIBLE);
        FrameLayout fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

        Fragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, true);
        newsFragment.setArguments(bundle);
        fragTransaction.add(fragment_container.getId(), newsFragment, NEWS_FRAGMENT_TAG);
        fragTransaction.commit();

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clear_search.setVisibility(View.VISIBLE);
                } else {
                    clear_search.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    closeKeyboard();

                    if (CommonUtil.isInternetConnectionAvailable(NewsSearchActivity.this)) {
                        performSearch(search.getText().toString().trim());
                    } else {
                        Toast.makeText(NewsSearchActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
//        checkForFilteredSearch(search, clear_search);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
    }

    private void performSearch(String celebrity_name) {

        NewsContentHandler newsContentHandler = NewsContentHandler.getInstance(NewsSearchActivity.this, NewsContentHandler.KEY_SEARCH_CONTENT);

        NewsFragment fragment = (NewsFragment) getSupportFragmentManager().findFragmentByTag(NEWS_FRAGMENT_TAG);
        if (fragment != null) {
            newsContentHandler.setSearchKeyword(celebrity_name);
            newsContentHandler.clearContent();

            fragment.showProgress(fragment.getView());
            newsContentHandler.refreshNews(true);
        } else {

        }
    }

    private void checkForFilteredSearch(EditText search, ImageView clear_search) {
        try {
            String searchString = getIntent().getStringExtra(Constants.FILTER_SEARCH_EXTRA);
            if (searchString.length() > 0) {
                search.setText(searchString, TextView.BufferType.EDITABLE);
                search.setSelection(searchString.length(), searchString.length());
                clear_search.setVisibility(View.VISIBLE);

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                performSearch(searchString);
            }
        } catch (NullPointerException stringisNull) {
            stringisNull.printStackTrace();
        }

    }
}
