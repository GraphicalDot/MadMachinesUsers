package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

import java.util.ArrayList;

/**
 * Created by madmachines on 8/9/15.
 */
public class ChatScreenAdapter extends BaseAdapter {

    ArrayList<SportsUnityDBHelper.Message> messageList;
    private static LayoutInflater inflater = null;
    public Activity activity;

    public ChatScreenAdapter(ChatScreenActivity chatScreenActivity, ArrayList<SportsUnityDBHelper.Message> messagelist) {
        this.messageList = messagelist;
        activity = chatScreenActivity;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        int flag;
        if (messageList.get(position).iAmSender) {
            flag = 1;
        } else {
            flag = 0;
        }
        return flag;
    }


    public static class ViewHolder {

        public TextView message;
        public TextView timeStamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        SportsUnityDBHelper.Message message = messageList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (getItemViewType(position)) {
                case 0:
                    vi = inflater.inflate(R.layout.chat_msg_recieve, parent, false);
                    holder.message = (TextView) vi.findViewById(R.id.singleMessageLeft);
                    holder.message.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoRegular());
                    holder.timeStamp = (TextView) vi.findViewById(R.id.timestampLeft);
                    holder.timeStamp.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoCondensedRegular());
                    vi.setTag(holder);
                    break;
                case 1:
                    vi = inflater.inflate(R.layout.chat_msg_send, parent, false);
                    holder.message = (TextView) vi.findViewById(R.id.singleMessageRight);
                    holder.message.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoRegular());
                    holder.timeStamp = (TextView) vi.findViewById(R.id.timestampRight);
                    holder.timeStamp.setTypeface(FontTypeface.getInstance(activity.getApplicationContext()).getRobotoCondensedRegular());

                    vi.setTag(holder);
                    break;
            }

        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.message.setText(message.msgTxt);
        switch (getItemViewType(position)) {
            case 0:
                holder.timeStamp.setText(message.send_Time);
                break;
            case 1:
                holder.timeStamp.setText(message.recieve_Time);
                break;

        }

        return vi;
    }

    public void notifydataset(ArrayList<SportsUnityDBHelper.Message> messagelist) {

        this.messageList = messagelist;
        notifyDataSetChanged();
    }
}
