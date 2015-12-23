package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.sports.unity.R;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.messages.controller.activity.NativeCameraActivity;
import com.sports.unity.messages.controller.model.Stickers;
import com.sports.unity.util.Constants;


import java.util.ArrayList;

/**
 * Created by amandeep on 17/11/15.
 */
public class ChatKeyboardHelper {

    private static ChatKeyboardHelper CHAT_KEYBOARD_HELPER = null;


    public static ChatKeyboardHelper getInstance(boolean newInstance) {
        if (CHAT_KEYBOARD_HELPER == null || newInstance) {
            CHAT_KEYBOARD_HELPER = new ChatKeyboardHelper();
        }
        return CHAT_KEYBOARD_HELPER;
    }

    public static void clean() {
        CHAT_KEYBOARD_HELPER = null;
    }

    private View popUpView;
    private PopupWindow popupWindow;

    private int keyboardHeight;
    private int previuosKeyboardHeight;
    private boolean isKeyBoardVisible = false;

    private ViewGroup parentLayout;

    private KeyboardOpenedListener keyboardOpenedListener = null;

    private AudioRecordingHelper audioRecordingHelper = null;

    private View lastTappedView = null;

    private ChatKeyboardHelper() {

    }

    public void createPopupWindowOnKeyBoard(ViewGroup parentLayout, Activity activity) {
        this.parentLayout = parentLayout;

        popUpView = activity.getLayoutInflater().inflate(R.layout.parent_layout_media_keyboard, null);
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, (int) keyboardHeight, false);
    }

    /**
     * Checking keyboard height and keyboard visibility
     */
    public void checkKeyboardHeight() {

        parentLayout.findViewById(R.id.type_msg).getViewTreeObserver().addOnGlobalLayoutListener(

                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight = parentLayout.getRootView().getHeight();
                        int heightDifference = screenHeight - (r.bottom);

                        if (previuosKeyboardHeight != heightDifference) {
                            changeKeyboardHeight(heightDifference, parentLayout);
                        }
                        previuosKeyboardHeight = heightDifference;

                        Log.i("Height Diff ", "" + heightDifference);

                        if (heightDifference > 100) {

                            if (!isKeyBoardVisible) {
                                Log.i("is KeyBoard Visible", "true");
                                isKeyBoardVisible = true;

                                {
                                    ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_text);
                                    int visibility = viewGroup.getVisibility();

                                    if (visibility == View.VISIBLE) {
                                        popupWindow.dismiss();

                                    } else {
                                        if (isAnyInputLayoutVisible()) {
                                            showPopupWindow(parentLayout);
                                        } else {
                                            viewGroup.setVisibility(View.VISIBLE);

                                            popupWindow.dismiss();

                                            View view = parentLayout.findViewById(R.id.btn_text);
                                            lastTappedView = view;
                                            highlightTappedItem( view);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (isKeyBoardVisible) {
                                Log.i("is KeyBoard Visible", "false");
                                isKeyBoardVisible = false;

                                popupWindow.dismiss();
                                unhighlightTappedItem(lastTappedView);

                                hideAllInputLayouts();

                                ViewGroup sendMessageLayout = (ViewGroup) parentLayout.findViewById(R.id.send_message_layout);
                                sendMessageLayout.setVisibility(View.VISIBLE);

                                cleanUp();
                            }
                        }
                    }

                });
    }

    public void tapOnTab(String sendToIdentity, View view, Activity activity){
        unhighlightTappedItem(lastTappedView);
        highlightTappedItem(view);
        lastTappedView = view;

        int id = view.getId();
        if( id == R.id.btn_text ){
            tapOnTextKeyBoard(activity);
        } else if( id == R.id.btn_camera ){
            tapOnCamera( sendToIdentity, activity);
        } else if( id == R.id.btn_gallery ){
            tapOnGallery(activity);
        } else if( id == R.id.btn_emoticons ){
            tapOnEmoji(activity);
        } else if( id == R.id.btn_audiomsg ){
            tapOnAudio(activity);
        }
    }

    private void unhighlightTappedItem(View view){
        if( view != null ){
            int id = view.getId();
            ImageButton imageButton = (ImageButton)view;

            if( id == R.id.btn_text ){
                imageButton.setImageResource(R.drawable.ic_keyboard_disabled);
            } else if( id == R.id.btn_camera ){
                imageButton.setImageResource( R.drawable.ic_camera_disabled);
            } else if( id == R.id.btn_gallery ){
                imageButton.setImageResource( R.drawable.ic_gallery_disabled);
            } else if( id == R.id.btn_emoticons ){
                imageButton.setImageResource( R.drawable.ic_emojis_disabled);
            } else if( id == R.id.btn_audiomsg ){
                imageButton.setImageResource( R.drawable.ic_mic_disabled);
            }
        }
    }

    private void highlightTappedItem(View view){
        if( view != null ){
            int id = view.getId();
            ImageButton imageButton = (ImageButton)view;

            if( id == R.id.btn_text ){
                imageButton.setImageResource(R.drawable.ic_keyboard_selected);
            } else if( id == R.id.btn_camera ){
                imageButton.setImageResource( R.drawable.ic_camera_pressed);
            } else if( id == R.id.btn_gallery ){
                imageButton.setImageResource( R.drawable.ic_gallery_pressed);
            } else if( id == R.id.btn_emoticons ){
                imageButton.setImageResource( R.drawable.ic_emojis_pressed);
            } else if( id == R.id.btn_audiomsg ){
                imageButton.setImageResource( R.drawable.ic_mic_pressed);
            }
        }
    }

    public void tapOnTextKeyBoard(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_text);
        int visibility = viewGroup.getVisibility();

        hideAllInputLayouts();
        if (isKeyBoardVisible) {
            if (visibility == View.VISIBLE) {
                toggleSystemKeyboard(parentLayout, activity.getApplicationContext());
            } else {
                viewGroup.setVisibility(View.VISIBLE);
                popupWindow.dismiss();

                postActionOnOpeningTextKeyboard(activity);
            }

        } else {
            toggleSystemKeyboard(parentLayout, activity.getApplicationContext());

            viewGroup.setVisibility(View.VISIBLE);
            postActionOnOpeningTextKeyboard(activity);
        }

    }

    public void tapOnCamera(String sendToIdentity, Activity activity) {
        ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_camera);
        int visibility = viewGroup.getVisibility();

        hideAllInputLayouts();
        if (isKeyBoardVisible) {
            if (visibility == View.VISIBLE) {
                toggleSystemKeyboard(parentLayout, activity.getApplicationContext());
            } else {
                viewGroup.setVisibility(View.VISIBLE);
                showPopupWindow(parentLayout);

                postActionOnOpeningCameraKeyboard(sendToIdentity, activity);
                toggleSystemKeyboard(parentLayout, activity.getApplicationContext());
            }

        } else {
            unhighlightTappedItem(lastTappedView);
            openNativeCameraActivity(sendToIdentity, activity);
        }
    }

    public void tapOnEmoji(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_emoji);
        int visibility = viewGroup.getVisibility();

        hideAllInputLayouts();
        if (isKeyBoardVisible) {
            if (visibility == View.VISIBLE) {
                toggleSystemKeyboard(parentLayout, activity.getApplicationContext());
            } else {
                viewGroup.setVisibility(View.VISIBLE);
                showPopupWindow(parentLayout);

                postActionOnOpeningEmojiKeyboard(activity, viewGroup);
            }

        } else {
            toggleSystemKeyboard(parentLayout, activity.getApplicationContext());

            viewGroup.setVisibility(View.VISIBLE);
            postActionOnOpeningEmojiKeyboard(activity, viewGroup);
        }
    }


    public void tapOnGallery(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_gallery);
        int visibility = viewGroup.getVisibility();

        final RecyclerView gallery = (RecyclerView) viewGroup.findViewById(com.sports.unity.R.id.my_recycler_view);
        gallery.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        gallery.setLayoutManager(mLayoutManager);

        hideAllInputLayouts();
        if (isKeyBoardVisible) {
            if (visibility == View.VISIBLE) {
                toggleSystemKeyboard(parentLayout, activity.getApplicationContext());
            } else {
                viewGroup.setVisibility(View.VISIBLE);
                showPopupWindow(parentLayout);

                postActionOnOpeningGalleryKeyboard(gallery, activity);
            }

        } else {
            toggleSystemKeyboard(parentLayout, activity.getApplicationContext());

            viewGroup.setVisibility(View.VISIBLE);

            postActionOnOpeningGalleryKeyboard(gallery, activity);
        }
    }

    public void tapOnAudio(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_voice);
        int visibility = viewGroup.getVisibility();

        hideAllInputLayouts();
        if (isKeyBoardVisible) {
            if (visibility == View.VISIBLE) {
                toggleSystemKeyboard(parentLayout, activity.getApplicationContext());
            } else {
                viewGroup.setVisibility(View.VISIBLE);
                showPopupWindow(parentLayout);

                postActionOnOpeningVoiceKeyboard(activity, viewGroup);
            }

        } else {
            toggleSystemKeyboard(parentLayout, activity.getApplicationContext());

            viewGroup.setVisibility(View.VISIBLE);
            postActionOnOpeningVoiceKeyboard(activity, viewGroup);
        }

    }

    private void postActionOnOpeningTextKeyboard(Activity activity) {
        ViewGroup sendMessageLayout = (ViewGroup) activity.findViewById(R.id.send_message_layout);
        sendMessageLayout.setVisibility(View.VISIBLE);

        EditText editText = (EditText) sendMessageLayout.findViewById(R.id.msg);
        editText.requestFocus();
    }

    private void postActionOnOpeningCameraKeyboard(String sendToIdentity, Activity activity) {
        ViewGroup sendMessageLayout = (ViewGroup) activity.findViewById(R.id.send_message_layout);
        sendMessageLayout.setVisibility(View.GONE);

        openNativeCameraActivity(sendToIdentity, activity);
    }

    private void postActionOnOpeningEmojiKeyboard(final Activity activity, ViewGroup viewGroup) {
        ViewGroup sendMessageLayout = (ViewGroup) activity.findViewById(R.id.send_message_layout);
        sendMessageLayout.setVisibility(View.GONE);

        new LoadStickers( activity.getBaseContext(), viewGroup).execute();

    }

    private void openNativeCameraActivity(String sendToIdentity, Activity activity){
        Intent intent = new Intent(activity, NativeCameraActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PHONE_NUMBER, sendToIdentity);
        activity.startActivity(intent);
    }

    private class LoadStickers extends AsyncTask {

        private Context context;
        private ViewGroup viewGroup;
        private ProgressBar progressBar;

        LoadStickers(Context context, ViewGroup viewGroup){
            this.context = context;
            this.viewGroup = viewGroup;
        }

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) viewGroup.findViewById(R.id.progress);
            progressBar.getIndeterminateDrawable().setColorFilter( popUpView.getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] params) {
            Stickers.getInstance().loadAllStickers(context);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            ViewPager pager = (ViewPager) viewGroup.findViewById(R.id.emojipager);
            SlidingTabLayout tabs = (SlidingTabLayout) viewGroup.findViewById(com.sports.unity.R.id.tabs);

            progressBar.setVisibility(View.GONE);
            pager.setAdapter( new AdapterForEmoji(context));

            tabs.setDistributeEvenly(true);
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

                @Override
                public int getIndicatorColor(int position) {
                    return context.getResources().getColor(com.sports.unity.R.color.tabsScrollColor);
                }

            });

            tabs.setCustomTabView(R.layout.custom_tab_view, 0);
            tabs.setViewPager(pager);
        }

    }

    private void postActionOnOpeningGalleryKeyboard(final RecyclerView gallery, final Activity activity) {
        ViewGroup sendMessageLayout = (ViewGroup) activity.findViewById(R.id.send_message_layout);
        sendMessageLayout.setVisibility(View.GONE);

        if (isKeyBoardVisible) {
            showGalleryView(activity, gallery);
        } else {
            keyboardOpenedListener = new KeyboardOpenedListener() {

                @Override
                public void keyboardOpened(int keyboardHeight) {
                    showGalleryView(activity, gallery);
                    keyboardOpenedListener = null;
                }

            };
        }
    }

    private void postActionOnOpeningVoiceKeyboard(Activity activity, ViewGroup viewGroup) {
        ViewGroup sendMessageLayout = (ViewGroup) activity.findViewById(R.id.send_message_layout);
        sendMessageLayout.setVisibility(View.GONE);

        audioRecordingHelper = AudioRecordingHelper.getInstance(activity);
        audioRecordingHelper.initView(viewGroup);
    }

    private void toggleSystemKeyboard(ViewGroup layout, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(layout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideAllInputLayouts() {
        ViewGroup contentView = ((ViewGroup) popupWindow.getContentView());
        for (int loop = 0; loop < contentView.getChildCount(); loop++) {
            contentView.getChildAt(loop).setVisibility(View.GONE);
        }
    }

    private boolean isAnyInputLayoutVisible() {
        boolean visible = false;

        ViewGroup contentView = ((ViewGroup) popupWindow.getContentView());
        for (int loop = 0; loop < contentView.getChildCount(); loop++) {
            if (contentView.getChildAt(loop).getVisibility() == View.VISIBLE) {
                visible = true;
                break;
            }
        }

        return visible;
    }

    private void changeKeyboardHeight(int height, View parentLayout) {
        if (height > 100) {
            keyboardHeight = height;
            popupWindow.setHeight(keyboardHeight);

            if (keyboardOpenedListener != null) {
                keyboardOpenedListener.keyboardOpened(keyboardHeight);
            }

            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                showPopupWindow(parentLayout);
            }
        }
    }

    private void showGalleryView(Activity activity, RecyclerView recyclerView) {
        ArrayList<String> path = getAllShownImagesPath(activity);

        RecyclerView.Adapter mAdapter = new ImageAdapterForGallery(activity, recyclerView, path, keyboardHeight);
        recyclerView.setAdapter(mAdapter);
    }

    private void showPopupWindow(View parentLayout) {
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
        } else {
            //nothing
        }
    }

    private void cleanUp(){
        audioRecordingHelper = null;
    }

    public static ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;

        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        cursor.close();
        return listOfAllImages;
    }

    private interface KeyboardOpenedListener {

        public void keyboardOpened(int keyboardHeight);

    }

}
