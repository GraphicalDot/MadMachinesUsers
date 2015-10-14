package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

import java.util.ArrayList;

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactListAdapter extends ArrayAdapter<SportsUnityDBHelper.Contacts> implements View.OnClickListener {

    private final Activity context;
    ArrayList<SportsUnityDBHelper.Contacts> contactsArrayList;
    Button invite;

    public ContactListAdapter(Activity context, int resource, ArrayList<SportsUnityDBHelper.Contacts> list) {
        super(context, R.layout.list_contact_msgs, list);
        this.context = context;
        this.contactsArrayList = list;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_contact_msgs, null, true);

        ImageView userIcon = (ImageView) rowView.findViewById(R.id.user_icon);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.contact_name);
        txtTitle.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        TextView status = (TextView) rowView.findViewById(R.id.status);
        status.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoLight());

        invite = (Button) rowView.findViewById(R.id.btn_invite);
        invite.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());
        invite.setOnClickListener(this);

        txtTitle.setText(contactsArrayList.get(position).name);
        status.setText(contactsArrayList.get(position).status);

        if (contactsArrayList.get(position).image != null) {
            userIcon.setImageBitmap(BitmapFactory.decodeByteArray(contactsArrayList.get(position).image, 0, contactsArrayList.get(position).image.length));
        }

        if (contactsArrayList.get(position).registered) {
            invite.setVisibility(View.INVISIBLE);
        }
        return rowView;

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
    }
}



