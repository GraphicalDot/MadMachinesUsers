package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by madmachines on 23/9/15.
 */
public class ChatListAdapter extends ArrayAdapter<Chats> implements StickyListHeadersAdapter {

    private final Activity context;
    private ArrayList<Chats> chatArrayList;
    private SimpleDateFormat formatter;
    private LayoutInflater inflater;

    private boolean isSearch;
    private ArrayList<Chats> contact;
    private ArrayList<Chats> messages;

    public ChatListAdapter(Activity context, int resource, ArrayList<Chats> chatList) {
        super(context, R.layout.list_chats_item, chatList);
        this.context = context;
        this.chatArrayList = chatList;
        formatter = new SimpleDateFormat("k:mm");
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_chats_item, null, true);

        Chats chats = chatArrayList.get(position);

        ImageView mediaIcon = (ImageView) rowView.findViewById(R.id.sentMediaIcon);

        TextView name = (TextView) rowView.findViewById(R.id.contact_name);
        name.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        ImageView discussIndicator = (ImageView) rowView.findViewById(R.id.discuss_indicator);

        ImageView userPic = (ImageView) rowView.findViewById(R.id.user_pic);
        if (!chats.isGroupChat) {
            discussIndicator.setVisibility(View.GONE);
            if (chatArrayList.get(position).image != null) {
                if (chatArrayList.get(position).image.length > 0) {
                    userPic.setImageBitmap(BitmapFactory.decodeByteArray(chatArrayList.get(position).image, 0, chatArrayList.get(position).image.length));
                } else {
                    userPic.setImageResource(R.drawable.ic_user);
                }
            } else {
                userPic.setImageResource(R.drawable.ic_user);
            }
        } else {
            if (!chatArrayList.get(position).jid.startsWith("DIS_")) {
                discussIndicator.setVisibility(View.GONE);
            } else {
                discussIndicator.setVisibility(View.VISIBLE);
            }
            if (chatArrayList.get(position).image == null) {
                userPic.setImageResource(R.drawable.ic_group);
            } else {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(chatArrayList.get(position).image, 0, chatArrayList.get(position).image.length));
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
                int days = CommonUtil.getTimeDifference(Long.parseLong(chatArrayList.get(position).sent));
                if (days > 0) {
                    if (days == 1) {
                        lastMsgTime.setText("YESTERDAY");
                    } else {
                        lastMsgTime.setText(CommonUtil.getDefaultTimezoneTime(Long.parseLong(chatArrayList.get(position).sent)));
                    }
                } else {

                    lastMsgTime.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(chatArrayList.get(position).sent)));
                }

            } else {
                int days = CommonUtil.getTimeDifference(Long.parseLong(chatArrayList.get(position).received));
                if (days > 0) {
                    if (days == 1) {
                        lastMsgTime.setText("YESTERDAY");
                    } else {
                        lastMsgTime.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(chatArrayList.get(position).received)));
                    }
                } else {

                    lastMsgTime.setText(CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(chatArrayList.get(position).received)));
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
        isSearch = false;
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

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        String headerText = "";
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.chat_search_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.list_header_title);
            holder.text.setTypeface(FontTypeface.getInstance(context).getRobotoMedium());
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout2);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        if (isSearch) {
            convertView.setVisibility(View.VISIBLE);
            if (position < contact.size()) {
                headerText = "Contacts";
            } else {
                headerText = "Messages";
            }
            holder.text.setText(headerText);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.text.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        if (isSearch) {
            if (position < contact.size()) {
                return (long) 0.0;
            } else {
                return (long) 1.0;
            }
        } else {
            return (long) 0.0;
        }

    }


    class HeaderViewHolder {
        LinearLayout linearLayout;
        TextView text;
    }

    public void updateSearch(ArrayList<Chats> contact, ArrayList<Chats> messages) {
        this.isSearch = true;
        this.contact = contact;
        this.messages = messages;
        chatArrayList.clear();
        chatArrayList.addAll(contact);
        chatArrayList.addAll(messages);
        super.notifyDataSetChanged();
    }
}
