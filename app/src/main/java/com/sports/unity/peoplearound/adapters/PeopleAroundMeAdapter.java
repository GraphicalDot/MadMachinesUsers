package com.sports.unity.peoplearound.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.User;

import java.util.ArrayList;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeAdapter extends ArrayAdapter<User> {

    private Activity context;
    private ArrayList<User> data;
    private int layoutResourceId;

    public PeopleAroundMeAdapter(Activity context, int resource, ArrayList<User> data) {
        super(context, resource);
        this.context = context;
        this.data = data;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, null, true);
        }

        User user = data.get(position);

        TextView name = (TextView) rowView.findViewById(R.id.tv_friend_name);
        TextView distance = (TextView) rowView.findViewById(R.id.tv_friend_distance);

        name.setText(user.getName());
        distance.setText(user.getDistance());

        return rowView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int position) {
        return data.get(position);
    }

    public void updateData(ArrayList<User> data) {
        this.data.clear();
        this.data.addAll(data);
        this.notifyDataSetChanged();
        super.notifyDataSetChanged();
    }
}
