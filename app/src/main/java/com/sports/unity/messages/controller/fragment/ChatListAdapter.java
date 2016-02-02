package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class ChatListAdapter extends ArrayAdapter<Chats> {

    private final Activity context;
    private ArrayList<Chats> chatArrayList;
    private SimpleDateFormat formatter;

    public ChatListAdapter(Activity context, int resource, ArrayList<Chats> chatList) {
        super(context, R.layout.list_chats_item, chatList);
        this.context = context;
        this.chatArrayList = chatList;
        formatter = new SimpleDateFormat("k:mm");
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_chats_item, null, true);

        Chats chats = chatArrayList.get(position);

        if (chats.groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            rowView.setTag(0);
        } else {
            rowView.setTag(1);
        }

        ImageView mediaIcon = (ImageView) rowView.findViewById(R.id.sentMediaIcon);

        TextView name = (TextView) rowView.findViewById(R.id.contact_name);
        name.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        ImageView userPic = (ImageView) rowView.findViewById(R.id.user_pic);
        if (chatArrayList.get(position).groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            if (chatArrayList.get(position).userImage != null) {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(chatArrayList.get(position).userImage, 0, chatArrayList.get(position).userImage.length));
            }

        } else {
            if (chatArrayList.get(position).groupImage == null) {
                userPic.setImageResource(R.drawable.ic_group);
            } else {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(chatArrayList.get(position).groupImage, 0, chatArrayList.get(position).groupImage.length));
            }
        }

        ImageView mute_icon = (ImageView) rowView.findViewById(R.id.mute_icon);

        if (chats.mute) {
            mute_icon.setVisibility(View.VISIBLE);

        } else {
            mute_icon.setVisibility(View.GONE);
        }

        TextView lastMsg = (TextView) rowView.findViewById(R.id.last_msg);

        TextView lastMsgTime = (TextView) rowView.findViewById(R.id.lastmsg_time);
        lastMsgTime.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoCondensedRegular());

        TextView unread = (TextView) rowView.findViewById(R.id.unread_count);
        unread.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoMedium());

        if (chatArrayList.get(position).data.equals("")) {
            lastMsgTime.setText("");
        } else {
            if (chatArrayList.get(position).sent != null && !chatArrayList.get(position).sent.equals("")) {
                int days = Integer.parseInt(CommonUtil.getTimeDifference(Long.parseLong(chatArrayList.get(position).sent)));
                if (days > 0) {
                    if (days == 1) {
                        lastMsgTime.setText("YESTERDAY");
                    } else {
                        lastMsgTime.setText(days + " days ago");
                    }
                } else {

                    lastMsgTime.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(chatArrayList.get(position).sent)));
                }

            } else {
                int days = Integer.parseInt(CommonUtil.getTimeDifference(Long.parseLong(chatArrayList.get(position).recieved)));
                if (days > 0) {
                    if (days == 1) {
                        lastMsgTime.setText("YESTERDAY");
                    } else {
                        lastMsgTime.setText(days + " days ago");
                    }
                } else {

                    lastMsgTime.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(chatArrayList.get(position).recieved)));
                }
            }
        }

        if (chatArrayList.get(position).unreadCount == 0) {
            unread.setVisibility(View.GONE);
        } else {
            unread.setVisibility(View.VISIBLE);
            unread.setText(String.valueOf(chatArrayList.get(position).unreadCount));
            lastMsgTime.setTextColor(unread.getResources().getColor(R.color.app_theme_blue));
        }


        if (chatArrayList.get(position).mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            mediaIcon.setVisibility(View.GONE);
            lastMsg.setText(chatArrayList.get(position).data);
        } else if (chatArrayList.get(position).mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            mediaIcon.setVisibility(View.VISIBLE);
            mediaIcon.setImageResource(R.drawable.ic_img);
            lastMsg.setText(R.string.sent_an_image);
        } else if (chatArrayList.get(position).mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            mediaIcon.setVisibility(View.VISIBLE);
            mediaIcon.setImageResource(R.drawable.ic_video);
            lastMsg.setText(R.string.sent_a_video);
        } else if (chatArrayList.get(position).mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            mediaIcon.setVisibility(View.GONE);
            lastMsg.setText(R.string.sent_a_sticker);
        } else if (chatArrayList.get(position).mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            mediaIcon.setImageResource(R.drawable.ic_audio_s);
            mediaIcon.setVisibility(View.VISIBLE);
            lastMsg.setText(R.string.sent_an_audio);
        }

        name.setText(chatArrayList.get(position).name);
        return rowView;
    }

    public void updateList(ArrayList<Chats> chatList) {
        this.chatArrayList.clear();
        this.chatArrayList.addAll(chatList);
        super.notifyDataSetChanged();
    }

    public void filter(String filterText) {
        if (filterText.length() == 0) {
            //do nothing
        } else {
            ArrayList<Chats> chats = getChatArrayList();
            chatArrayList.clear();
            for (Chats c :
                    chats) {
                if (c.name.contains(filterText)) {
                    chatArrayList.add(c);
                }
            }
        }
        super.notifyDataSetChanged();
    }

    public ArrayList<Chats> getChatArrayList() {
        return chatArrayList;
    }

}
