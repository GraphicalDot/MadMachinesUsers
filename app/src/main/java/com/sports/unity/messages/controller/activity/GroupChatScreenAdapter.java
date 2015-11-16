package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by madmachines on 12/11/15.
 */
public class GroupChatScreenAdapter extends BaseAdapter {

    ArrayList<Message> messageList;
    private static LayoutInflater inflater = null;
    public Activity activity;

    public GroupChatScreenAdapter(ChatScreenActivity chatScreenActivity, ArrayList<Message> messagelist) {
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

        public TextView sender;
        public TextView message;
        public TextView timeStamp;
        public ImageView receivedStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        Message message = messageList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (getItemViewType(position)) {
                case 0:
                    vi = inflater.inflate(R.layout.group_chat_receive, parent, false);
                    holder.message = (TextView) vi.findViewById(R.id.singleMessageLeft);
                    holder.message.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
                    holder.timeStamp = (TextView) vi.findViewById(R.id.timestampLeft);
                    holder.timeStamp.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
                    holder.sender = (TextView) vi.findViewById(R.id.group_sender_name);
                    holder.sender.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                    holder.receivedStatus = null;

                    vi.setTag(holder);
                    break;
                case 1:
                    vi = inflater.inflate(R.layout.chat_msg_send, parent, false);
                    holder.message = (TextView) vi.findViewById(R.id.singleMessageRight);
                    holder.message.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
                    holder.timeStamp = (TextView) vi.findViewById(R.id.timestampRight);
                    holder.timeStamp.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
                    holder.receivedStatus = (ImageView) vi.findViewById(R.id.receivedStatus);
                    vi.setTag(holder);
                    holder.sender = null;
                    break;
            }

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
        holder.message.setText(message.textData);
        switch (getItemViewType(position)) {
            case 0:
                holder.timeStamp.setText(String.valueOf(new java.text.SimpleDateFormat("HH:mm").format(Long.valueOf(message.recieveTime))));
                break;
            case 1:
                holder.timeStamp.setText(String.valueOf(new java.text.SimpleDateFormat("HH:mm").format(Long.valueOf(message.recieveTime))));
                break;

        }
        if (holder.sender != null) {
            String name = SportsUnityDBHelper.getInstance(activity).getName(messageList.get(position).number);
            if (name == null) {
                holder.sender.setText(messageList.get(position).number);
            } else {
                holder.sender.setText(name);
            }
        }

        if (holder.receivedStatus == null) {
            //do nothing
        } else {

            if (message.serverR != null) {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_sent);
            }
            if (message.recipientR != null) {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_delivered);
            }
            if (message.serverR == null && message.recipientR == null) {
                holder.receivedStatus.setImageResource(R.drawable.ic_msg_pending);
            }
        }

        return vi;
    }

    public void notifydataset(ArrayList<Message> messagelist) {
        this.messageList = messagelist;
        notifyDataSetChanged();
    }

}
