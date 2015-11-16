package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Chats;

import java.util.ArrayList;

/**
 * Created by madmachines on 30/10/15.
 */
public class ChatFragmentDialogListAdapter extends BaseAdapter {


    private static final String MUTE = "Mute Conversation";
    private static LayoutInflater inflater = null;

    private ArrayList<String> chatMenuOptions;
    private Activity activity;

    private Chats chat;


    public ChatFragmentDialogListAdapter(ArrayList<String> menuOptions, Activity context, Chats chatObject) {
        this.chatMenuOptions = menuOptions;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = context;
        this.chat = chatObject;

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
                    vi.setTag(holder);
                    break;
                case 1:
                    vi = inflater.inflate(R.layout.fragment_chat_mute_switcher, parent, false);
                    holder.muteSwitch = (Switch) vi.findViewById(R.id.mute_switcher);
                    holder.muteSwitch.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
                    holder.muteSwitch.setText(chatMenuOptions.get(position));
                    holder.textOption = null;
                    vi.setTag(holder);
                    break;
            }
        }else {
            holder = (ViewHolder) vi.getTag();
        }

        if (null == holder.muteSwitch) {
            //nothing
        } else {
            if (chat.mute) {
                holder.muteSwitch.setChecked(true);
            } else {
                holder.muteSwitch.setChecked(false);
            }
        }

        return vi;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
