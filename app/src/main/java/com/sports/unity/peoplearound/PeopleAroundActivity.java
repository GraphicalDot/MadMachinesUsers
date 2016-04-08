package com.sports.unity.peoplearound;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scores.model.ScoresJsonParser;

import java.util.ArrayList;

public class PeopleAroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_around);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);
        /*getExtras();
        initView();*/
        setToolbar();

    }
    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }
    private void setTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView title_text = (TextView) toolbar.findViewById(R.id.toolbar_title);

        StringBuilder stringBuilder = new StringBuilder();

        try {} catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
