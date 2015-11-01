package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class ChatListAdapter extends ArrayAdapter<SportsUnityDBHelper.Chats> {

    private final Activity context;
    private ArrayList<SportsUnityDBHelper.Chats> chatArrayList;
    private SimpleDateFormat formatter;
    private DateTime dateTime;
    private DateTime dateTime1;

    public ChatListAdapter(Activity context, int resource, ArrayList<SportsUnityDBHelper.Chats> chatList) {
        super(context, R.layout.list_chats_item, chatList);
        this.context = context;
        this.chatArrayList = chatList;
        formatter = new SimpleDateFormat("k:mm");
        dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_chats_item, null, true);

        TextView name = (TextView) rowView.findViewById(R.id.contact_name);
        name.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        ImageView userPic = (ImageView) rowView.findViewById(R.id.user_pic);
        if (chatArrayList.get(position).userImage != null) {
            userPic.setImageBitmap(BitmapFactory.decodeByteArray(chatArrayList.get(position).userImage, 0, chatArrayList.get(position).userImage.length));
        }

        TextView lastMsg = (TextView) rowView.findViewById(R.id.last_msg);

        TextView lastMsgTime = (TextView) rowView.findViewById(R.id.lastmsg_time);
        lastMsgTime.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoCondensedRegular());

        TextView unread = (TextView) rowView.findViewById(R.id.unread_count);
        unread.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoMedium());

        //DateTime currentDateTime = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());

        if (chatArrayList.get(position).sent != null && !chatArrayList.get(position).sent.equals("")) {
            dateTime = new DateTime(Long.valueOf(chatArrayList.get(position).sent));
            Log.i("dateTime", String.valueOf(dateTime));
            int days = Days.daysBetween(dateTime, dateTime1).getDays();
            if (days > 0) {
                if (days == 1) {
                    lastMsgTime.setText("YESTERDAY");
                } else {
                    lastMsgTime.setText(days + "ago");
                }
            } else {

                lastMsgTime.setText(String.valueOf(new java.text.SimpleDateFormat("HH:mm").format(Long.valueOf(chatArrayList.get(position).sent))));
            }

        } else {
            dateTime = new DateTime(Long.valueOf(chatArrayList.get(position).recieved));
            int days = Days.daysBetween(dateTime, dateTime1).getDays();
            if (days > 0) {
                if (days == 1) {
                    lastMsgTime.setText("YESTERDAY");
                } else {
                    lastMsgTime.setText(days + "ago");
                }
            } else {

                lastMsgTime.setText(String.valueOf(new java.text.SimpleDateFormat("HH:mm").format(Long.valueOf(chatArrayList.get(position).recieved))));
            }
        }

        if (chatArrayList.get(position).unreadCount == 0) {
            unread.setVisibility(View.INVISIBLE);
        } else {
            unread.setText(String.valueOf(chatArrayList.get(position).unreadCount));
            lastMsgTime.setTextColor(Color.parseColor("#2c84cc"));

        }
        lastMsg.setText(chatArrayList.get(position).data);
        name.setText(chatArrayList.get(position).userName);
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
