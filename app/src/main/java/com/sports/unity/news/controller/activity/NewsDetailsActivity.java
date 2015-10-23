package com.sports.unity.news.controller.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;

public class NewsDetailsActivity extends AppCompatActivity {

    private String content = "";
    private String url = "";
    private String title = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_details);
        setToolBar();

        url = getIntent().getStringExtra("Url");
        title = getIntent().getStringExtra("title");
        content = title + " " + url;

        initViews();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(com.sports.unity.R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv.setText("Sports Unity");
    }

    private void initViews() {
        WebView webview = (WebView) findViewById(R.id.webview);

        ImageView img=(ImageView) findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.getSettings().setBuiltInZoomControls(true);
        webview.loadUrl(url);
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
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sports Unity");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,content);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
