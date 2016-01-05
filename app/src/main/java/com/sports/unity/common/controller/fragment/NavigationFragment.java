package com.sports.unity.common.controller.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.Contacts;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mad on 1/4/2016.
 */
public class NavigationFragment extends Fragment {


    private static XMPPTCPConnection con;
    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(getActivity());
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_nav,container,false);
        con = XMPPClient.getConnection();

        setNavigationProfile(view);

        return view;
    }

    public void setNavigationProfile(View view) {

        LinearLayout navigationView = (LinearLayout) view.findViewById(R.id.nav_header);

        CircleImageView profile_photo = (CircleImageView) navigationView.findViewById(R.id.circleView);
        TextView name = (TextView) navigationView.findViewById(R.id.name);

        String user_name = TinyDB.getInstance(getActivity()).getString(TinyDB.KEY_USERNAME);

        Contacts contact = sportsUnityDBHelper.getContact(user_name);


        if(contact.image != null) {
             Bitmap bmp = BitmapFactory.decodeByteArray(contact.image, 0, contact.image.length);
             profile_photo.setImageBitmap(bmp);
        } else {
            profile_photo.setImageResource(R.drawable.ic_user);
        }


        name.setText(contact.name);

    }
}
