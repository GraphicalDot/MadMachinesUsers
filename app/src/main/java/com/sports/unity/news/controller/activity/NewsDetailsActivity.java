package com.sports.unity.news.controller.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

public class NewsDetailsActivity extends CustomAppCompatActivity {

    private String content = null;

    private String url = null;
    private String title = null;
    private String type = null;

    private ProgressBar progressBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_details);

        url = getIntent().getStringExtra(Constants.INTENT_KEY_URL);
        title = getIntent().getStringExtra(Constants.INTENT_KEY_TITLE);
        type = getIntent().getStringExtra(Constants.INTENT_KEY_TYPE);

        content = title + "\n\n" + url;

        setToolBar();
        initViews();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(com.sports.unity.R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView tv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv.setText(type);
        tv.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
    }

    private void initViews() {

        TextView titleText = (TextView) findViewById(R.id.TitleText);
        TextView InfoData = (TextView) findViewById(R.id.info_text);
        TextView published = (TextView) findViewById(R.id.published);
        TextView sportType = (TextView) findViewById(R.id.type);
        ImageView image = (ImageView) findViewById(R.id.img_url);
        ImageView fabIcon = (ImageView) findViewById(R.id.fab_icon);


        titleText.setText(title);
        InfoData.setText(content);
        sportType.setText(type);


        ImageView img = (ImageView) findViewById(R.id.img);
        img.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
