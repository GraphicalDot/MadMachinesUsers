package com.sports.unity.common.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by anupam x on 27/4/16.
 */
public class FriendRequestsActivityAdapter extends ArrayAdapter<Contacts> {

    private Activity context;
    private ArrayList<Contacts> contacts;
    private int layoutResourceId;

    public FriendRequestsActivityAdapter(Activity context, int resource, ArrayList<Contacts> contacts) {
        super(context, resource);
        this.contacts = contacts;
        this.context = context;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, null, true);
        }

        Contacts contact = contacts.get(position);

        Resources resources = getContext().getResources();

        LinearLayout actionOnPendingRequest = (LinearLayout) rowView.findViewById(R.id.action_friend_request);
        LinearLayout parentLayout = (LinearLayout) rowView.findViewById(R.id.parent_layout);
        ImageView userPic = (ImageView) rowView.findViewById(R.id.user_icon);
        TextView name = (TextView) rowView.findViewById(R.id.contact_name);
        TextView status = (TextView) rowView.findViewById(R.id.status);
        Button confirm = (Button) rowView.findViewById(R.id.confirm);
        Button block = (Button) rowView.findViewById(R.id.block);

        confirm.setTypeface(FontTypeface.getInstance(context).getRobotoCondensedRegular());
        block.setTypeface(FontTypeface.getInstance(context).getRobotoCondensedRegular());

        confirm.setOnClickListener(onClickListener);
        block.setOnClickListener(onClickListener);
        userPic.setOnClickListener(onClickListener);

        userPic.setTag(position);
        confirm.setTag(position);
        block.setTag(position);

        if (contact.requestStatus == Contacts.REQUEST_ACCEPTED) {
            status.setVisibility(View.VISIBLE);
            actionOnPendingRequest.setVisibility(View.GONE);

            status.setText(R.string.you_are_friends);

            name.setTextColor(resources.getColor(R.color.app_theme_blue));
            status.setTextColor(resources.getColor(android.R.color.black));
            parentLayout.setBackground(new ColorDrawable(resources.getColor(R.color.accepted_friend_request)));
        } else if (contact.requestStatus == Contacts.REQUEST_BLOCKED) {
            status.setVisibility(View.VISIBLE);
            actionOnPendingRequest.setVisibility(View.GONE);

            status.setText(contact.getName() + " is blocked ");

            name.setTextColor(resources.getColor(R.color.app_theme_blue));
            status.setTextColor(resources.getColor(android.R.color.black));
            parentLayout.setBackground(new ColorDrawable(resources.getColor(R.color.accepted_friend_request)));
        } else {

            status.setVisibility(View.GONE);
            actionOnPendingRequest.setVisibility(View.VISIBLE);
            parentLayout.setBackground(new ColorDrawable(resources.getColor(android.R.color.white)));
        }

        if (contact.image != null)

        {
            if (contact.image.length > 0) {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(contact.image, 0, contact.image.length));
            } else {
                userPic.setImageResource(R.drawable.ic_user);
            }
        } else

        {
            userPic.setImageResource(R.drawable.ic_user);
        }

        name.setText(contact.getName());

        return rowView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.user_icon) {
                openUserProfile(v);
            } else if (v.getId() == R.id.confirm) {
                sendAcceptance(v);
            } else if (v.getId() == R.id.block) {
                blockUser(v);
            }

        }
    };

    private void blockUser(View v) {
        Contacts contact = contacts.get((Integer) v.getTag());
        BlockUnblockUserHelper blockUnblockUserHelper = new BlockUnblockUserHelper(contact.blockStatus, context, null);
        blockUnblockUserHelper.addBlockUnblockListener(((FriendRequestsActivity) context));
        blockUnblockUserHelper.onMenuItemSelected(context, contact.id, contact.jid, null);
    }

    private void sendAcceptance(View v) {
        if (XMPPClient.getInstance().isConnectionAuthenticated()) {
            Contacts contact = contacts.get((Integer) v.getTag());
            boolean success = PersonalMessaging.getInstance(context).acceptFriendRequest(contact.jid);
            if (success) {
                ((Button) v).setText(R.string.accepting_friend);
                v.setEnabled(false);
            }
        } else {
            Toast.makeText(context, R.string.conn_not_authenticated, Toast.LENGTH_SHORT).show();
        }
    }

    private void openUserProfile(View v) {
        Contacts contact = contacts.get((Integer) v.getTag());
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("name", contact.getName());
        intent.putExtra("profilePicture", contact.image);
        intent.putExtra("jid", contact.jid);
        intent.putExtra("status", contact.status);
        intent.putExtra("otherChat", contact.isOthers());
//        intent.putExtra(INTENT_KEY_CONTACT_AVAILABLE_STATUS, contactAvailableStatus);
        context.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_PROFILE);
    }

    public ArrayList<Contacts> getContactsArrayList() {
        return contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contacts getItem(int position) {
        return contacts.get(position);
    }

    public void updateList(ArrayList<Contacts> contacts) {
        this.contacts.clear();
        this.contacts.addAll(contacts);
        this.notifyDataSetChanged();
    }
}
