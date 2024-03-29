package com.sports.unity.peoplearound.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.User;
import com.sports.unity.peoplearound.PeopleAroundActivity;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.network.FirebaseUtil;

import java.util.ArrayList;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeAdapter extends ArrayAdapter<User> {

    private Activity context;
    private ArrayList<User> users;
    private int layoutResourceId;
    private String TAG;

    public PeopleAroundMeAdapter(Activity context, int resource, ArrayList<User> users, String TAG) {
        super(context, resource);
        this.context = context;
        this.users = users;
        this.layoutResourceId = resource;
        this.TAG = TAG;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, null, true);
        }

        User user = users.get(position);

        TextView name = (TextView) rowView.findViewById(R.id.tv_friend_name);
        TextView distance = (TextView) rowView.findViewById(R.id.tv_friend_distance);
        TextView onlineStatus = (TextView) rowView.findViewById(R.id.online_status);
        ImageView statusIndicator = (ImageView) rowView.findViewById(R.id.status_indicator);

        if (TAG == PeopleAroundActivity.FRIENDS_KEY) {
            String username = SportsUnityDBHelper.getInstance(context).getNameByJIDFromAvailableContacts(user.getJid());
            if (username == null) {
                username = user.getName();
            }
            name.setText(username);
        } else {

            name.setText(user.getName());
        }


        if (String.valueOf(user.getDistance()).length() > 3) {
            distance.setText("Approx " + String.valueOf(Math.round(user.getDistance() / 1000)) + " kms away");
        } else {
            distance.setText("Approx " + String.valueOf(user.getDistance()) + " mts away");
        }

        if (user.isUserOnline()) {
            onlineStatus.setText("Online");
            statusIndicator.setImageResource(R.drawable.online_indicator_dot);
        } else {
            String lastSeen = checkForDelta(user.getLastSeen());
            long lastSeenEpoch = Long.parseLong(lastSeen);
            int days = CommonUtil.getTimeDifference(lastSeenEpoch);
            if (days == 0) {
                onlineStatus.setText("last seen today at  " + CommonUtil.getDefaultTimezoneTimeInAMANDPM(lastSeenEpoch));
            } else if (days == 1) {
                onlineStatus.setText("last seen yesterday at  " + CommonUtil.getDefaultTimezoneTimeInAMANDPM(lastSeenEpoch));
            } else {
                onlineStatus.setText("last seen " + days + " days ago");
            }
            statusIndicator.setImageResource(R.drawable.offline_indicator_dot);
        }

        rowView.setTag(position);
        rowView.setOnClickListener(onClickListener);
        return rowView;
    }

    private String checkForDelta(String lastSeen) {
        if (lastSeen.contains(".")) {
            lastSeen = lastSeen.substring(0, lastSeen.indexOf("."));
        }
        return lastSeen;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            User user = users.get((Integer) v.getTag());
            Contacts contact = SportsUnityDBHelper.getInstance(getContext()).getContactByJid(user.getJid());
            if (contact == null) {
                createContact(user.getJid(), context, user.getName());
                contact = SportsUnityDBHelper.getInstance(getContext()).getContactByJid(user.getJid());
                moveToChatActivity(contact);
            } else {
                if (contact.availableStatus != Contacts.AVAILABLE_BY_MY_CONTACTS) {
                    SportsUnityDBHelper.getInstance(context).updateContactName(contact.id, user.getName());
                    contact = SportsUnityDBHelper.getInstance(getContext()).getContactByJid(user.getJid());
                }
                moveToChatActivity(contact);
            }
        }
    };

    private void createContact(String jid, Context context, String name) {
        SportsUnityDBHelper.getInstance(context).addToContacts(name, null, jid, ContactsHandler.getInstance().defaultStatus, null, Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME);
    }
    private void logScreensToFireBase(String eventName) {
        //FIREBASE INTEGRATION
        {
            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(context);
            Bundle bundle = new Bundle();
            FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName);
        }
    }
    private void moveToChatActivity(Contacts contact) {
        if(TAG.equalsIgnoreCase(PeopleAroundActivity.FRIENDS_KEY)){
            logScreensToFireBase(FirebaseUtil.Event.PAM_FRIEND_CHAT);
        }else if(TAG.equalsIgnoreCase(PeopleAroundActivity.SPU_KEY)){
            logScreensToFireBase(FirebaseUtil.Event.PAM_SU_CHAT);
        }else if(TAG.equalsIgnoreCase(PeopleAroundActivity.SIMILAR_USERS_KEY)){
            logScreensToFireBase(FirebaseUtil.Event.PAM_SIMILAR_CHAT);
        }
        String name = contact.getName();
        int contactId = contact.id;
        byte[] userPicture = contact.image;
        boolean blockStatus = SportsUnityDBHelper.getInstance(context).isChatBlocked(contactId);
        boolean othersChat = contact.isOthers();

        Intent intent = ChatScreenActivity.createChatScreenIntent(context,
                false,
                contact.jid,
                name, contact.id,
                userPicture,
                blockStatus,
                othersChat,
                contact.availableStatus,
                contact.status);
        context.startActivity(intent);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    public void updateData(ArrayList<User> data) {
        this.users.clear();
        this.users.addAll(data);
        this.notifyDataSetChanged();
        super.notifyDataSetChanged();
    }
}
