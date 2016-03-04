package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class GetCountryCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_country_code);

        setToolBar();
        setInitView();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.cancel).setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        toolbar.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_filter);
        title.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        title.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());
        title.setText("Select Country");

        TextView next = (TextView) toolbar.findViewById(R.id.toolbar_title);
        next.setVisibility(View.GONE);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setInitView() {

        String[] countryListArray=this.getResources().getStringArray(R.array.CountryCodes);

        final ArrayList<String> countryNameList = new ArrayList<>();
        final ArrayList<String> countryCodeList = new ArrayList<>();

        for(int i=0;i<countryListArray.length;i++){
            String[] code=countryListArray[i].split(",");
            countryCodeList.add(code[0]);
            countryNameList.add(code[2]);
        }



        RecyclerView countryList = (RecyclerView) findViewById(R.id.country_list);
        countryList.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(GetCountryCode.this);
        countryList.setLayoutManager(mLayoutManager);

        String selectedCountryName = getIntent().getStringExtra("CountryName");



        final CountryCodeAdapter adapter = new CountryCodeAdapter(selectedCountryName,countryNameList,countryCodeList);
        countryList.setAdapter(adapter);

        final EditText searchCountry = (EditText) findViewById(R.id.searchCountry);

        searchCountry.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    adapter.refreshList(countryNameList,countryCodeList);
                } else {
                    adapter.getFilter().filter(s.toString());
                    adapter.getFilter().filter(s.toString());
                }
            }

        });
    }

    public class CountryCodeAdapter extends RecyclerView.Adapter implements Filterable {

        int selected_position;
        ArrayList<String> usedCountryName;
        ArrayList<String> usedCountryCode;
        private CountryFilter filter;
        String selectedCountryName = "";

        public CountryCodeAdapter(String selectedCountryName, ArrayList<String> countryNameList, ArrayList<String> countryCodeList) {
            filter=new CountryFilter(countryNameList,countryCodeList);
            usedCountryName = countryNameList;
            usedCountryCode = countryCodeList;
            this.selectedCountryName = selectedCountryName;
        }

        public void refreshList(ArrayList<String> countryNameList, ArrayList<String> countryCodeList) {
            usedCountryName = countryNameList;
            usedCountryCode = countryCodeList;
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView countryName;
            public TextView countryCode;
            public LinearLayout countryRow;

            public ViewHolder(View v) {
                super(v);
                countryName = (TextView) v.findViewById(com.sports.unity.R.id.name);
                countryCode = (TextView) v.findViewById(com.sports.unity.R.id.code);
                countryRow = (LinearLayout) v.findViewById(R.id.countryRow);

                countryName.setTypeface(FontTypeface.getInstance(getBaseContext()).getRobotoSlabRegular());
                countryCode.setTypeface(FontTypeface.getInstance(getBaseContext()).getRobotoRegular());


            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_list_with_code, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            selected_position = -1;
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.countryName.setText(usedCountryName.get(position));
            viewHolder.countryCode.setText("+"+usedCountryCode.get(position));


            if( usedCountryName.get(position).equals(selectedCountryName)) {
                selected_position = position;
            }

            viewHolder.countryRow.setTag(position);

            if (selected_position == position) {
                viewHolder.countryName.setTextColor(getResources().getColor(R.color.app_theme_blue));
                viewHolder.countryCode.setTextColor(getResources().getColor(R.color.app_theme_blue));
            } else {
                viewHolder.countryName.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
                viewHolder.countryCode.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
            }

            viewHolder.countryRow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int position = (Integer) view.getTag();

                    notifyItemChanged(selected_position);
                    selected_position = position;
                    selectedCountryName = usedCountryName.get(position);
                    notifyItemChanged(selected_position);

                    Intent intent = new Intent();
                    intent.putExtra("countryName",viewHolder.countryName.getText());
                    intent.putExtra("countryCode", viewHolder.countryCode.getText().toString().replace("+",""));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

        }

        @Override
        public int getItemCount() {
            return usedCountryName.size();
        }

        private class CountryFilter extends Filter {

            ArrayList<String> countryNameList;
            ArrayList<String> countryCodeList;

            public CountryFilter(ArrayList<String> countryNameList, ArrayList<String> countryCodeList) {
                this.countryCodeList = countryCodeList;
                this.countryNameList = countryNameList;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchQwery=constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                ArrayList<String>finalName= countryNameList;
                ArrayList<String>finalCode= countryCodeList;
                usedCountryCode=new ArrayList<>();
                usedCountryName=new ArrayList<>();
                ArrayList<String> searchName = new ArrayList<String>();
                for(int i=0;i<finalName.size();i++){
                    if(finalName.get(i).toLowerCase().contains(searchQwery)){
                        usedCountryCode.add(finalCode.get(i));
                        usedCountryName.add(finalName.get(i));                    }
                }
                results.values=usedCountryCode;
                results.count=usedCountryCode.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                CountryCodeAdapter.this.notifyDataSetChanged();
            }
        }
    }

}
