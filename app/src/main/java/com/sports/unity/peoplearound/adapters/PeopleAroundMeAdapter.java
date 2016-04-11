package com.sports.unity.peoplearound.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Person;


import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeAdapter extends RecyclerView.Adapter<PeopleAroundMeAdapter.ViewHolder> {

    private ArrayList<Person> people;
    private Context context;
    String userName;

    public PeopleAroundMeAdapter(ArrayList<Person> people,Context context) {
        this.people = people;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_people_aroundme_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.dto = people.get(position);
        userName = holder.dto.getUsername()+ "@mm.io";
        holder.tvfriendname.setText(holder.dto.getName());
        int distance = holder.dto.getDistance();
        if (distance > 1000) {
            float dist = distance /= 1000;
            holder.tvfrienddistance.setText(String.valueOf(dist) + " kms ");
        } else {
            holder.tvfrienddistance.setText(String.valueOf(distance) + " mts ");
        }
        Glide.with(context).load(holder.dto.getUsername()).placeholder(R.drawable.ic_no_img).into(holder.ivfriendimg);



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contacts contact = SportsUnityDBHelper.getInstance(context).getContactByJid(userName);
                if (contact == null) {
                    //createContact(jid, context, vCard);
                    contact = SportsUnityDBHelper.getInstance(context).getContactByJid(userName);
                    moveToChatActivity(contact, false);
                } else {
                    moveToChatActivity(contact, true);
                }
            }
        });
    }










    @Override
    public int getItemCount() {
        return people.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView tvfriendname;
        public TextView tvfrienddistance;
        public ImageView ivfriendimg;

        public final View mView;

        public Person dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvfriendname=(TextView) view.findViewById(R.id.tv_friend_name);
            tvfrienddistance=(TextView) view.findViewById(R.id.tv_friend_distance);
            ivfriendimg=(ImageView) view.findViewById(R.id.iv_friend_img);


        }
    }

    private void moveToChatActivity(Contacts contact, boolean contactAvailable) {
        String name = contact.getName();
        int contactId = contact.id;
        byte[] userPicture = contact.image;
        boolean nearbyChat = false;
        boolean blockStatus = SportsUnityDBHelper.getInstance(context).isChatBlocked(contactId);
        boolean othersChat = contact.isOthers();

        Intent intent = ChatScreenActivity.createChatScreenIntent(context, false, contact.jid, name, contact.id, userPicture, blockStatus, othersChat, contact.availableStatus, contact.status);
        context.startActivity(intent);
    }
    private boolean createContact(String jid, Context context, VCard vCard) {
        boolean success = false;
        SportsUnityDBHelper.getInstance(context).addToContacts(vCard.getNickName(), null, jid, ContactsHandler.getInstance().defaultStatus, null, Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME);
        SportsUnityDBHelper.getInstance(context).updateContacts(jid, vCard.getAvatar(), vCard.getMiddleName());
        return success;
    }
}
