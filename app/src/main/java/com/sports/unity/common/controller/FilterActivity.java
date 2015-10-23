package com.sports.unity.common.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;


import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private int[] sportsCategoryLayoutID = new int[]{ R.id.basketball, R.id.cricket, R.id.football, R.id.f1, R.id.tennis };
    private boolean[] checkedFlag = new boolean[]{ false, false, false, false, false };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);
        setToolBar();

        initCheckedFlagList();
        initViews();
    }

    private void initCheckedFlagList(){
        ArrayList<String> filter = UserUtil.getSportsSelected();
        if( filter.contains(Constants.GAME_KEY_BASKETBALL) ){
            checkedFlag[0] = true;
        }
        if( filter.contains(Constants.GAME_KEY_CRICKET) ){
            checkedFlag[1] = true;
        }
        if( filter.contains(Constants.GAME_KEY_FOOTBALL) ){
            checkedFlag[2] = true;
        }
        if( filter.contains(Constants.GAME_KEY_F1) ){
            checkedFlag[3] = true;
        }
        if( filter.contains(Constants.GAME_KEY_TENNIS) ){
            checkedFlag[4] = true;
        }
    }

    private void saveFilterlist(){
        ArrayList<String> filter = new ArrayList<>();

        if( checkedFlag[0] ){
            filter.add(Constants.GAME_KEY_BASKETBALL);
        }
        if( checkedFlag[1] ){
            filter.add(Constants.GAME_KEY_CRICKET);
        }
        if( checkedFlag[2] ){
            filter.add(Constants.GAME_KEY_FOOTBALL);
        }
        if( checkedFlag[3] ){
            filter.add(Constants.GAME_KEY_F1);
        }
        if( checkedFlag[4] ){
            filter.add(Constants.GAME_KEY_TENNIS);
        }

        UserUtil.setSportsSelected(this, filter);
    }

    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveOn(false);
            }
        });

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveOn(true);
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initViews(){
        TextView clearrFilter=(TextView) findViewById(R.id.clear);
        clearrFilter.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        TextView filterBySports=(TextView) findViewById(R.id.filter);
        filterBySports.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        TextView advanceFilter=(TextView) findViewById(R.id.filter3);
        advanceFilter.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        for( int loop=0; loop < sportsCategoryLayoutID.length ; loop++ ){
            initCheckBox( sportsCategoryLayoutID[loop], checkedFlag[loop], loop);
        }

        clearrFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }

    private void initCheckBox( int layoutId, boolean checked, int index){
        LinearLayout layout = (LinearLayout)findViewById(layoutId);
        layout.setTag(index);

        initTextViewBasedOnCheckFlag(layout, checked);

        CheckBox checkbox = (CheckBox)layout.getChildAt(2);
        checkbox.setChecked(checked);
    }

    private void initTextViewBasedOnCheckFlag(LinearLayout layout, boolean checked){
        TextView title = (TextView)layout.getChildAt(1);
        if ( checked ) {
            title.setTextColor(Color.parseColor("#2c84cc"));
        } else {
            title.setTextColor(Color.parseColor("#7d7d7d"));
        }
    }

    private int checkedItemCount(){
        int count = 0;
        for( int loop=0; loop < sportsCategoryLayoutID.length ; loop++ ){
            if( checkedFlag[loop] == true ){
                count++;
            }
        }
        return count;
    }

    private void onClickCheckBox( int layoutId){
        LinearLayout layout = (LinearLayout)findViewById(layoutId);
        int index = (Integer)layout.getTag();

        boolean checked = checkedFlag[index];

        if( checked && checkedItemCount() == 1 ) {
            Toast.makeText( this, R.string.keep_one_sport_selected, Toast.LENGTH_SHORT).show();
        } else {
            checkedFlag[index] = !checked;

            CheckBox checkbox = (CheckBox) layout.getChildAt(2);
            checkbox.setChecked(checkedFlag[index]);

            initTextViewBasedOnCheckFlag(layout, checkedFlag[index]);
        }
    }

    private void moveOn( boolean saveFilter){
        if( saveFilter == true ) {
            saveFilterlist();
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            finish();
        }

    }

    public void onCheckboxClicked(View view) {
        onClickCheckBox(view.getId());
    }

}
