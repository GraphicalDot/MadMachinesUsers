package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

import java.util.ArrayList;

/**
 * Created by madmachines on 30/10/15.
 */
public class ChatFragmentDialogListAdapter extends BaseAdapter {


    private static final String MUTE = "Mute Conversation";
    private static LayoutInflater inflater = null;

    private ArrayList<String> chatMenuOptions;
    private Activity activity;


    public ChatFragmentDialogListAdapter(ArrayList<String> menuOptions, Activity context) {
        this.chatMenuOptions = menuOptions;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = context;

    }

    @Override
    public int getCount() {
        return chatMenuOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        int flag;
        if (chatMenuOptions.get(position).equals(MUTE)) {
            flag = 1;
        } else {
            flag = 0;
        }
        return flag;
    }

    public static class ViewHolder {

        public TextView textOption;
        public Switch muteSwitch;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            switch (getItemViewType(position)) {
                case 0:
                    vi = inflater.inflate(R.layout.fragment_chat_simple_option, parent, false);
                    holder.textOption = (TextView) vi.findViewById(R.id.simple_menu_option);
                    holder.textOption.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
                    holder.textOption.setText(chatMenuOptions.get(position));
                    holder.muteSwitch = null;
                    break;
                case 1:
                    vi = inflater.inflate(R.layout.fragment_chat_mute_switcher, parent, false);
                    holder.muteSwitch = (Switch) vi.findViewById(R.id.mute_switcher);
                    holder.muteSwitch.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
                    holder.muteSwitch.setText(chatMenuOptions.get(position));
                    holder.textOption = null;
                    break;
            }
        }

        return vi;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
