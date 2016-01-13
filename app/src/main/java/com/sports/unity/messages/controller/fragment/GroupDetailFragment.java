package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.activity.CreateGroup;
import com.sports.unity.messages.controller.model.GroupMessaging;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by amandeep on 30/10/15.
 */
public class GroupDetailFragment extends Fragment {

    static final int LOAD_IMAGE_GALLERY_CAMERA = 1;
    private CircleImageView groupAvatar;
    private byte[] groupImageArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        setToolBar();
        initView(view);

        return view;
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((ImageView) toolbar.findViewById(R.id.backImage)).setImageResource(R.drawable.ic_close_blk);
        toolbar.findViewById(R.id.backImage).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setText(R.string.group_title_create);

        TextView actionView = (TextView) toolbar.findViewById(R.id.actionButton);
        actionView.setText(R.string.next);
        actionView.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedBold());
        actionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                moveOn(view);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void initView(View view) {

        groupAvatar = (CircleImageView) view.findViewById(R.id.group_image);
        groupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, LOAD_IMAGE_GALLERY_CAMERA);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE_GALLERY_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap decodedBitmap = ImageUtil.handleImageAndSetToView(data, groupAvatar);

            groupImageArray = ImageUtil.getBytes(decodedBitmap);
        } else {
            //callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void moveOn(View view) {
        EditText groupNameEditText = (EditText) getView().findViewById(R.id.groupName);

        String groupName = groupNameEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(getActivity(), R.string.group_message_provide_group_name, Toast.LENGTH_SHORT).show();
        } else {
            CreateGroup createGroupActivity = ((CreateGroup) getActivity());
            createGroupActivity.setGroupDetails(groupName, "", groupImageArray);

            createGroupActivity.moveToMembersListFragment();
        }
    }

}
