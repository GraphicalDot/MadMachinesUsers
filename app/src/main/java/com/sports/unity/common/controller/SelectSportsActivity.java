package com.sports.unity.common.controller;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.SportsGridViewAdapter;

public class SelectSportsActivity extends AppCompatActivity {

    Toolbar toolbar;
    GridView gridview;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sports);

        toolbar = (Toolbar) findViewById(com.sports.unity.R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Select your favourite sports");
        mTitle.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "RobotoCondensed-Regular.ttf"));

        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new SportsGridViewAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_sports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
