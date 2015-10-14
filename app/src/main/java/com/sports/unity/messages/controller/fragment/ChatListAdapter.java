package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class ChatListAdapter extends ArrayAdapter<SportsUnityDBHelper.Chats> {

    private final Activity context;
    private ArrayList<SportsUnityDBHelper.Chats> chatArrayList;

    public ChatListAdapter(Activity context, int resource, ArrayList<SportsUnityDBHelper.Chats> chatList) {
        super(context, R.layout.list_chats_item, chatList);
        this.context = context;
        this.chatArrayList = chatList;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_chats_item, null, true);

        TextView name = (TextView) rowView.findViewById(R.id.contact_name);
        name.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        TextView lastMsg = (TextView) rowView.findViewById(R.id.last_msg);

        TextView lastMsgTime = (TextView) rowView.findViewById(R.id.lastmsg_time);
        lastMsgTime.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoCondensedRegular());

        TextView unread = (TextView) rowView.findViewById(R.id.unread_count);
        unread.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoMedium());

        //DateTime currentDateTime = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());

        if (chatArrayList.get(position).sentTime != null) {
            //int days = Days.daysBetween(dateTime, currentDateTime).getDays();
            lastMsgTime.setText(chatArrayList.get(position).sentTime);
        } else {
            lastMsgTime.setText(chatArrayList.get(position).recieveTime);
        }

        if (chatArrayList.get(position).unreadCount == 0) {
            unread.setVisibility(View.INVISIBLE);
        } else {
            unread.setText(String.valueOf(chatArrayList.get(position).unreadCount));
            lastMsgTime.setTextColor(Color.parseColor("#2c84cc"));

        }
        lastMsg.setText(chatArrayList.get(position).msg);
        name.setText(chatArrayList.get(position).name);
        return rowView;
    }

    public void updateList(ArrayList<SportsUnityDBHelper.Chats> chatList) {
        this.chatArrayList.clear();
        this.chatArrayList.addAll(chatList);
        super.notifyDataSetChanged();
    }

    public ArrayList<SportsUnityDBHelper.Chats> getChatArrayList() {
        return chatArrayList;
    }

}
