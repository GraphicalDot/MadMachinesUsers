package com.sports.unity.messages.controller.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.model.Contacts;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mad on 2/22/2016.
 */
public class GroupParticipantsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Contacts> dataSet;
    private boolean isAdmin = false;

    public GroupParticipantsAdapter(Context context, ArrayList<Contacts> dataSet, boolean isAdmin) {
        this.context = context;
        this.dataSet = dataSet;
        this.isAdmin = isAdmin;

        if( isAdmin ){
            this.dataSet = new ArrayList<>();
            this.dataSet.add( new Contacts( "", "", "", null, -1, null, 0));
            this.dataSet.addAll(dataSet);
        } else {
            this.dataSet = dataSet;
        }
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return new Object();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.groupinfoitem, null);
        }

        CircleImageView userImage = (CircleImageView) convertView.findViewById(R.id.user_image);
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        TextView userStatus = (TextView) convertView.findViewById(R.id.user_status);
        TextView admin = (TextView) convertView.findViewById(R.id.admin);


        userName.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());
        userStatus.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());
        admin.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());
        userStatus.setTypeface(FontTypeface.getInstance(context.getApplicationContext()).getRobotoRegular());

        if ( isAdmin && position == 0) {

            userImage.setImageResource(R.drawable.ic_add_contact);
            userImage.setBorderColor(context.getResources().getColor(R.color.app_theme_blue));

            userName.setText(R.string.add_friends);
            userStatus.setText(R.string.add_msg);

            admin.setVisibility(View.GONE);
        } else {
            Contacts c = dataSet.get(position);
            if (c.image != null) {
                userImage.setImageBitmap(BitmapFactory.decodeByteArray(c.image, 0, c.image.length));
            } else {
                userImage.setImageResource(R.drawable.ic_user);
            }
            userImage.setBorderColor(context.getResources().getColor(R.color.gray4));

            userName.setText(c.name);
            userStatus.setText(c.status);

            admin.setVisibility(View.GONE);
        }
        return convertView;
    }

}
