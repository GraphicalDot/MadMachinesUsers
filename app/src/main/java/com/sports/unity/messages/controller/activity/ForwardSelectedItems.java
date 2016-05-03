package com.sports.unity.messages.controller.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.messages.controller.fragment.ChatFragment;
import com.sports.unity.messages.controller.fragment.ContactsFragment;
import com.sports.unity.messages.controller.fragment.OthersFragment;
import com.sports.unity.messages.controller.model.ShareableData;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class ForwardSelectedItems extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private int screenHeight;
    private int screenWidth;
    private ArrayList<ShareableData> dataList = new ArrayList<>();

    public static final String KEY_FILES_NOT_SENT = "files_too_large";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout tabs;

    private int filesNotSent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_selected_items);
        initToolbar();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorPrimary);
            }
        });

        tabs.setViewPager(mViewPager);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;

        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            checkForImplicitIntent();
        } else {
            if (PermissionUtil.getInstance().requestPermission(this, new ArrayList<String>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.gallery_permission_message), Constants.REQUEST_CODE_GALLERY_STORAGE_PERMISSION)) {
                checkForImplicitIntent();
            }
        }

    }

    private void checkForImplicitIntent() {

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //TODO handle writing to file events in thread

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent, dataList); // Handle text being sent
            } else {

                Uri URI = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                long filesize = ImageUtil.getFileSize(getApplicationContext(), URI, URI.getScheme());

                if (filesize > 5120 && filesize <= 10485760) {

                    handleMediaContent(dataList, URI);

                } else {

                    //do nothing

                }
            }

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {

            if (intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM).size() > 10) {

                Toast.makeText(getApplicationContext(), "Can't send more than 10 items at once", Toast.LENGTH_LONG).show();
                this.finish();

            } else {

                handleSendMultipleItems(intent, dataList);

            }

        } else {

            dataList = intent.getParcelableArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS);
        }

        if (dataList.isEmpty()) {

            Toast.makeText(getApplicationContext(), " Too big file to send, please select another ", Toast.LENGTH_LONG).show();
            this.finish();

        } else {
//            if (filesNotSent > 0) {
//                Toast.makeText(getApplicationContext(), getResources().getQuantityString(R.plurals.file_count, filesNotSent, filesNotSent), Toast.LENGTH_LONG).show();
//            }
            //do nothing
        }

    }

    private void handleSendText(Intent intent, ArrayList<ShareableData> dataList) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            dataList.add(new ShareableData(SportsUnityDBHelper.MIME_TYPE_TEXT, sharedText, ""));
        }
    }

    private void handleMediaContent(ArrayList<ShareableData> dataList, Uri URI) {
        if (URI != null) {
            Log.i("URI", URI.toString());
            String filename = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_AUDIO, false);
            if (ImageUtil.getMimeType(URI).contains("image")) {
                try {

                    String path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Images.Media.DATA);
                    byte[] content = ImageUtil.getCompressedBytes(path, screenHeight, screenWidth);
                    DBUtil.writeContentToExternalFileStorage(getApplicationContext(), filename, content, SportsUnityDBHelper.MIME_TYPE_IMAGE);
                    dataList.add(new ShareableData(SportsUnityDBHelper.MIME_TYPE_IMAGE, "", filename));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (ImageUtil.getMimeType(URI).contains("audio")) {

                String path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Audio.Media.DATA);
                byte[] content = getByteArrayFromPath(path);
                DBUtil.writeContentToExternalFileStorage(getApplicationContext(), filename, content, SportsUnityDBHelper.MIME_TYPE_AUDIO);
                dataList.add(new ShareableData(SportsUnityDBHelper.MIME_TYPE_AUDIO, "", filename));

            } else if (ImageUtil.getMimeType(URI).contains("video")) {

                String path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Video.Media.DATA);
                byte[] content = getByteArrayFromPath(path);
                DBUtil.writeContentToExternalFileStorage(getApplicationContext(), filename, content, SportsUnityDBHelper.MIME_TYPE_VIDEO);
                dataList.add(new ShareableData(SportsUnityDBHelper.MIME_TYPE_VIDEO, "", filename));

            }
        } else {

            //nothing

        }
    }

    private void handleSendMultipleItems(Intent intent, ArrayList<ShareableData> dataList) {
        ArrayList<Uri> URIs = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (URIs != null) {
            for (Uri URI : URIs) {

                long filesize = ImageUtil.getFileSize(getApplicationContext(), URI, URI.getScheme());

                if (filesize > 5120 && filesize <= 10485760) {

                    handleMediaContent(dataList, URI);

                } else {

                    filesNotSent++;

                }
            }
        }
    }


    private static byte[] getByteArrayFromPath(String path) {
        File file = new File(path);
        byte[] content = null;
        FileInputStream in = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            in = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] chunk = new byte[(int) file.length()];
            int read = 0;
            while ((read = in.read(chunk)) != -1) {
                byteArrayOutputStream.write(chunk, 0, read);
            }

            content = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (Exception e) {
            }
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        return content;
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ImageView backArrow = (ImageView) findViewById(R.id.backarrow);
        backArrow.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    private void addContactFragment(ArrayList<ShareableData> forwardSelectedIds) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_FORWARD);
//        bundle.putParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS, forwardSelectedIds);
//
//        ContactsFragment fragment = new ContactsFragment();
//        fragment.setArguments(bundle);
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, fragment).commit();
//
//    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INTENT_KEY_FRIENDS_FRAGMENT_USAGE, ChatFragment.USAGE_FOR_SHARE_OR_FORWARD);
                bundle.putParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS, dataList);
                bundle.putInt(KEY_FILES_NOT_SENT, filesNotSent);

                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                return chatFragment;
            } else if (position == 1) {

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_FORWARD);
                bundle.putParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS, dataList);
                bundle.putInt(KEY_FILES_NOT_SENT, filesNotSent);

                ContactsFragment fragment = new ContactsFragment();
                fragment.setArguments(bundle);

                return fragment;
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INTENT_KEY_FRIENDS_FRAGMENT_USAGE, ChatFragment.USAGE_FOR_SHARE_OR_FORWARD);
                bundle.putParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS, dataList);
                bundle.putInt(KEY_FILES_NOT_SENT, filesNotSent);

                OthersFragment othersFragment = new OthersFragment();
                othersFragment.setArguments(bundle);

                return othersFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.friends);
                case 1:
                    return getResources().getString(R.string.contacts);
                case 2:
                    return getResources().getString(R.string.others);
            }
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_GALLERY_STORAGE_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                checkForImplicitIntent();
            } else {
                Toast.makeText(getApplicationContext(), " Permissions were not granted ", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}



