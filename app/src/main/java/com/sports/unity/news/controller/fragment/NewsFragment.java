package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.controller.GlobalSearchActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.news.controller.activity.NewsSearchActivity;
import com.sports.unity.news.model.NewsContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.FirebaseUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class NewsFragment extends Fragment implements NewsContentHandler.ContentListener {

    private NewsContentHandler newsContentHandler;

    private BaseNewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean loading = true;

    private boolean searchOn = false;

    private int sportsSelectedNum = 0;
    private ArrayList<String> sportSelected;
    private final String Showcase_News_Id = "news_showcase_id";

    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle = "Latest News";
    private String mDescription = "Curated and Compiled news from all major sport columns, just for your pocket.";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CommonUtil.sendAnalyticsData(getActivity().getApplication(), "NewsScreen");
        mUrl = Uri.parse("android-app://co.sports.unity/mobileapp/sportsunity.co/news");
        mClient = CommonUtil.getAppIndexingClient(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        Action action = CommonUtil.getAction(mTitle, mDescription, mUrl);
        CommonUtil.startAppIndexing(mClient, action);
    }

    @Override
    public void onStop() {
        Action action = CommonUtil.getAction(mTitle, mDescription, mUrl);
        CommonUtil.stopAppIndexing(mClient, action);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        searchOn = getArguments().getBoolean(Constants.INTENT_KEY_SEARCH_ON);

        View v = inflater.inflate(com.sports.unity.R.layout.news, container, false);
        initViews(v);
        if (UserUtil.getNewsFilterSportsSelected() != null) {
            sportsSelectedNum = UserUtil.getNewsFilterSportsSelected().size();
        }
        sportSelected = UserUtil.getNewsFilterSportsSelected();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        newsContentHandler.addContentListener(this);

        if (newsContentHandler.isRequestInProgress()) {
            showProgress(getView());
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            hideProgress(getView());
            mSwipeRefreshLayout.setRefreshing(false);
        }

        handleIfSportsChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        newsContentHandler.removeContentListener();

        sportsSelectedNum = UserUtil.getNewsFilterSportsSelected().size();
        sportSelected = UserUtil.getNewsFilterSportsSelected();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_menu, menu);

        if ((TinyDB.getInstance(getActivity()).getBoolean("check", false))) {
            menu.findItem(R.id.mini_cards).setChecked(true);
            menu.findItem(R.id.mini_cards).setIcon(R.drawable.ic_thumb);
        } else {
            menu.findItem(R.id.mini_cards).setChecked(false);
            menu.findItem(R.id.mini_cards).setIcon(R.drawable.ic_list);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void logScreensToFireBase(String screen) {
        //FIREBASE INTEGRATION
        {
            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getActivity());
            Bundle bundle = new Bundle();
            FirebaseUtil.logEvent(firebaseAnalytics, bundle, screen);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            logScreensToFireBase(FirebaseUtil.Event.NEWS_SEARCH);
            Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
            intent.putExtra(Constants.INTENT_KEY_GLOBAL_POSITION, 1);
            startActivity(intent);
            return true;
        }

        if (id == com.sports.unity.R.id.action_filter) {
            logScreensToFireBase(FirebaseUtil.Event.NEWS_FILTER);
            Intent i = new Intent(getActivity(), FilterActivity.class);
            i.putExtra(Constants.KEY_ORIGIN_ACTIVITY, Constants.NEWS_ACTIVITY);
            startActivityForResult(i, Constants.REQUEST_CODE_NEWS);
            return true;
        }

        if (id == R.id.mini_cards) {
            if (item.isChecked()) {
                item.setChecked(false);
                item.setIcon(R.drawable.ic_list);
                TinyDB.getInstance(getActivity()).putBoolean("check", false);
            } else {
                item.setChecked(true);
                item.setIcon(R.drawable.ic_thumb);
                TinyDB.getInstance(getActivity()).putBoolean("check", true);
            }
            logScreensToFireBase(FirebaseUtil.Event.NEWS_CARD);
            addOrUpdateAdapter();
            displayResult();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param v as view
     */
    private void initViews(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_theme_blue));

        mRecyclerView = (RecyclerView) v.findViewById(com.sports.unity.R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initProgress(v);
        hideProgress(v);

        hideErrorLayout(v);

        if (searchOn == false) {
            newsContentHandler = NewsContentHandler.getInstance(getActivity().getBaseContext(), NewsContentHandler.KEY_BASE_CONTENT);
        } else {
            newsContentHandler = NewsContentHandler.getInstance(getActivity().getBaseContext(), NewsContentHandler.KEY_SEARCH_CONTENT);
        }

        addOrUpdateAdapter();

        if (searchOn == false) {
            boolean success = newsContentHandler.refreshNews(false);
            if (success) {
                showProgress(v);
            }
        } else {
            String keyword = getArguments().getString("search_keyword");
            String searchKeyword = null;
            if (keyword != null) {

                if (keyword.contains(",")) {
                    searchKeyword = keyword.split(",")[0];
                } else {
                    searchKeyword = keyword;
                }
            } else {
                //nothing
            }

            newsContentHandler.setSearchKeyword(searchKeyword);

            if (newsContentHandler.getSearchKeyword() != null && newsContentHandler.getSearchKeyword().length() > 0) {
                newsContentHandler.clearContent();
                newsContentHandler.refreshNews(true);
            }
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("News Content", "Refresh Call");

                        boolean canRefreshContent = false;
                        if (searchOn) {
                            if (newsContentHandler.getSearchKeyword() != null && newsContentHandler.getSearchKeyword().length() > 0) {
                                canRefreshContent = true;
                            } else {
                                canRefreshContent = false;
                            }
                        } else {
                            canRefreshContent = true;
                        }

                        if (canRefreshContent) {
                            boolean success = newsContentHandler.refreshNews(true);
                            if (success == false) {
                                mSwipeRefreshLayout.setRefreshing(false);

                                Toast.makeText(getActivity(), R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
                            } else {
                                mSwipeRefreshLayout.setRefreshing(true);
//                              showProgress(getView());
                            }

                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                });
            }

        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int visibleThreshold = 5;
            private int previousTotal = 0;

//            boolean hideToolBar = false;
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (hideToolBar) {
//                    ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//                } else {
//                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//                }
//            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                if (dy > 20) {
//                    hideToolBar = true;
//
//                } else if (dy < -5) {
//                    hideToolBar = false;
//                }

                int visibleItemCount = mRecyclerView.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    Log.i("News Content", "Load More Call");

                    newsContentHandler.loadMoreNews();
                    loading = true;
                }
            }
        });

    }

    private void handleIfSportsChanged() {
        boolean isSportsChanged = false;
        ArrayList<String> sports = UserUtil.getNewsFilterSportsSelected();
        if (sportsSelectedNum != sports.size()) {
            isSportsChanged = true;
        } else {
            for (int i = 0; i < sportSelected.size(); i++) {
                if (!sports.contains(sportSelected.get(i))) {
                    isSportsChanged = true;
                }
            }
        }
        if (isSportsChanged) {
            newsContentHandler.clearContent();
            mAdapter.notifyDataSetChanged();
            newsContentHandler.selectedSportsChanged();
            sportSelected = sports;
            sportsSelectedNum = sports.size();

            boolean success = newsContentHandler.refreshNews(true);
            if (success == false) {
                showErrorLayout(getView());
                hideProgress(getView());
            } else {
                hideErrorLayout(getView());
                showProgress(getView());
            }
        }
    }

    private void showErrorLayout(View view) {
        if (mAdapter.getNews().size() == 0) {
            logScreensToFireBase(FirebaseUtil.Event.DATA_ERROR);
            ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.VISIBLE);
            CustomComponentListener.renderAppropriateErrorLayout(errorLayout);
        } else {
            Toast.makeText(getActivity(), R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideErrorLayout(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);
    }

    private void initProgress(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void showProgress(View view) {
        if (mAdapter.getNews().size() == 0) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }

    private void resetScrollFlag() {
        loading = false;
    }

    private void displayResult() {
        Log.i("News Content", "Display Result");

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ArrayList<JSONObject> list = mAdapter.getNews();
                Log.i("Adapter size", "Count:" + list.size());

                if (list.size() == 0) {
                    if (!searchOn) {
                        showErrorLayout(getView());
                    } else {
                        Toast.makeText(getActivity(), "No search results found for this search", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideErrorLayout(getView());
                    addOrUpdateAdapter();
                }

            }

        });
    }

    private void addOrUpdateAdapter() {
        boolean flag = TinyDB.getInstance(getActivity()).getBoolean("check", false);

        ArrayList list = null;
        if (flag) {
            if (mAdapter == null) {
                Log.d("News Content", "creating mini adapter");
                list = new ArrayList();
                mAdapter = new NewsMinicardAdapter(list, getActivity());
                mRecyclerView.setAdapter(mAdapter);

                newsContentHandler.init(list, searchOn);
            } else {
                if (mAdapter instanceof NewsMinicardAdapter) {
                    Log.d("News Content", "no change in mini adapter");
                } else {
                    Log.d("News Content", "creating mini adapter");

                    list = mAdapter.getNews();

                    mAdapter = new NewsMinicardAdapter(list, getActivity());
                    mRecyclerView.setAdapter(mAdapter);

                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            if (mAdapter == null) {
                Log.d("News Content", "creating news adapter");
                list = new ArrayList();
                mAdapter = new NewsAdapter(list, getActivity());
                mRecyclerView.setAdapter(mAdapter);

                newsContentHandler.init(list, searchOn);
            } else {
                if (mAdapter instanceof NewsAdapter) {
                    Log.d("News Content", "no change in news adapter");
                } else {
                    Log.d("News Content", "creating news adapter");

                    list = mAdapter.getNews();

                    mAdapter = new NewsAdapter(list, getActivity());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            list = mAdapter.getNews();
            Log.d("News Content", "Update Adapter List Object ID " + list);

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void handleContent(int responseCode) {
        resetScrollFlag();

        hideProgress(getView());
        mSwipeRefreshLayout.setRefreshing(false);

        if (responseCode == 1) {
            Log.i("News Content", "Handle Response");

            displayResult();
        } else if (responseCode == 0) {
            Log.i("News Content", "Handle Error");

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showErrorLayout(getView());
                }

            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), Showcase_News_Id);
        final ViewTreeObserver viewTreeObserver = getActivity().getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Activity activity = getActivity();
                if (activity != null) {
                    View menuButton = getActivity().findViewById(R.id.action_filter);
                    View menuButton1 = getActivity().findViewById(R.id.action_search);
                    View menuButton2 = getActivity().findViewById(R.id.mini_cards);
                    if (menuButton != null) {
                        sequence.addSequenceItem(menuButton, getActivity().getResources().getString(R.string.showcase_filter_heading), getActivity().getResources().getString(R.string.showcase_filter_message), getActivity().getResources().getString(R.string.next_showcase_preview_text));
                        if (viewTreeObserver.isAlive())
                            viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                    if (menuButton1 != null) {
                        sequence.addSequenceItem(menuButton1, getActivity().getResources().getString(R.string.showcase_search_heading), getActivity().getResources().getString(R.string.showcase_search_message), getActivity().getResources().getString(R.string.next_showcase_preview_text));
                        if (viewTreeObserver.isAlive())
                            viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                    if (menuButton2 != null) {
                        sequence.addSequenceItem(menuButton2, getActivity().getResources().getString(R.string.showcase_mini_card_heading), getActivity().getResources().getString(R.string.showcase_mini_card_message), getActivity().getResources().getString(R.string.next_showcase_preview_text));
                        sequence.start();
                        if (viewTreeObserver.isAlive())
                            viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                }
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}