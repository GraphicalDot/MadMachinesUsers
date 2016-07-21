package com.sports.unity.messages.controller.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.CropImageFragment;
import com.sports.unity.R;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.activity.GroupDetailActivity;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;
import com.sports.unity.util.network.FirebaseUtil;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by amandeep on 30/10/15.
 */
public class GroupCreateFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int LOAD_IMAGE_GALLERY_CAMERA = 1;

    private CircleImageView groupAvatar;
    private byte[] groupImage;
    private boolean nameClick = false;

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

        ((ImageView) toolbar.findViewById(R.id.backarrow)).setImageResource(R.drawable.ic_menu_back);
        toolbar.findViewById(R.id.backarrow).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.group_title_create);

        TextView actionView = (TextView) toolbar.findViewById(R.id.actionButton);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText(R.string.next);
        actionView.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedBold());
        actionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logScreensToFireBase(FirebaseUtil.Event.GROUP_NEXT);
                moveOn(view);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void logScreensToFireBase(String eventName) {
        //FIREBASE INTEGRATION
        {
            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getActivity());
            Bundle bundle = new Bundle();
            FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName);
        }
    }

    private void initView(View view) {

        groupAvatar = (CircleImageView) view.findViewById(R.id.group_image);
        if (groupImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(groupImage, 0, groupImage.length);
            groupAvatar.setImageBitmap(bitmap);
        }
        groupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logScreensToFireBase(FirebaseUtil.Event.GROUP_IMAGE);
                if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                    openImagePicker(GroupCreateFragment.this);
                } else {
                    if (PermissionUtil.getInstance().requestPermission(getActivity(), new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.camera_and_external_storage_permission_message), Constants.REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION)) {
                        openImagePicker(GroupCreateFragment.this);
                    }
                }
            }
        });
        EditText groupNameEditText = (EditText) view.findViewById(R.id.groupName);
        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!nameClick) {
                    nameClick = true;
                    logScreensToFireBase(FirebaseUtil.Event.GROUP_NAME);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE_GALLERY_CAMERA && resultCode == Activity.RESULT_OK) {
            byte[] groupImage = ImageUtil.handleImageAndSetToView(data, groupAvatar, ImageUtil.FULL_IMAGE_SIZE, ImageUtil.FULL_IMAGE_SIZE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(groupImage, 0, groupImage.length);
            ((GroupDetailActivity) getActivity()).initiateCrop(bitmap, GroupCreateFragment.this);
            manageToolbarForCrop(true);
        } else {
            //nothing
        }
    }

    private void manageToolbarForCrop(boolean isEditing) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        TextView actionView = (TextView) toolbar.findViewById(R.id.actionButton);
        if (!isEditing) {
            title.setVisibility(View.VISIBLE);
            actionView.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
            actionView.setVisibility(View.GONE);
        }

    }

    private void moveOn(View view) {
        EditText groupNameEditText = (EditText) getView().findViewById(R.id.groupName);
        groupNameEditText.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);

        String groupName = groupNameEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(getActivity(), R.string.group_message_provide_group_name, Toast.LENGTH_SHORT).show();
        } else {
            GroupDetailActivity groupDetailActivity = ((GroupDetailActivity) getActivity());
            groupDetailActivity.setGroupDetails(groupName, "", groupImage);

            groupDetailActivity.moveToMembersListFragment(GroupCreateFragment.this);
        }
    }

    public static void openImagePicker(Fragment fragment) {
        View view = fragment.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        fragment.startActivityForResult(chooser, LOAD_IMAGE_GALLERY_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                openImagePicker(this);
            } else {
                PermissionUtil.getInstance().showSnackBar(getActivity(), getString(R.string.permission_denied));
            }
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        groupImage = ImageUtil.getCompressedBytes(bitmap);
    }
}
