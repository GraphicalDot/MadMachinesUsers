package com.sports.unity.peoplearound.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Person;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeAdapter extends RecyclerView.Adapter<PeopleAroundMeAdapter.ViewHolder> {

    private ArrayList<Person> people;
    private Context context;


    public PeopleAroundMeAdapter(ArrayList<Person> people, Context context) {
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
        final String userName ;
        final String name;
        holder.dto = people.get(position);
        userName = holder.dto.getUsername();
        Log.i( "USERNAME: ",userName);
        name = holder.dto.getName();
        holder.tvfriendname.setText(holder.dto.getName());
        int distance = holder.dto.getDistance();
        if (distance > 1000) {
            float dist = distance /= 1000;
            holder.tvfrienddistance.setText(String.valueOf(dist) + " kms ");
        } else {
            holder.tvfrienddistance.setText(String.valueOf(distance) + " mts ");
        }


        Glide.with(context).load(holder.dto.getUsername()).placeholder(R.drawable.ic_user).into(holder.ivfriendimg);


        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Contacts contact = SportsUnityDBHelper.getInstance(context).getContactByJid(userName);
                if (contact == null) {
                    //createContact(userName, context, vCard);
                    createContact(userName, context,name);
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvfriendname;
        public TextView tvfrienddistance;
        public ImageView ivfriendimg;

        public final View mView;

        public Person dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvfriendname = (TextView) view.findViewById(R.id.tv_friend_name);
            tvfrienddistance = (TextView) view.findViewById(R.id.tv_friend_distance);
            ivfriendimg = (ImageView) view.findViewById(R.id.iv_friend_img);


        }
    }

    private void moveToChatActivity(Contacts contact, boolean contactAvailable) {
        int contactId = contact.id;
        byte[] userPicture = contact.image;
        boolean blockStatus = SportsUnityDBHelper.getInstance(context).isChatBlocked(contactId);
        boolean othersChat = contact.isOthers();

        Intent intent = ChatScreenActivity.createChatScreenIntent(context, false, contact.jid, contact.getName(), contact.id, userPicture, blockStatus, othersChat, contact.availableStatus, contact.status);
        context.startActivity(intent);
    }

    private boolean createContact(String jid, Context context,String name) {
        boolean success = false;
        byte[] emptyAvatar = new byte[0];
        SportsUnityDBHelper.getInstance(context).addToContacts(name, null, jid, ContactsHandler.getInstance().defaultStatus, null, Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME);
        SportsUnityDBHelper.getInstance(context).updateContacts(jid, emptyAvatar, name);
        return success;
    }






}
