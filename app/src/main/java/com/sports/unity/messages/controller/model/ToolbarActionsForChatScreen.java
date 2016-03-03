package com.sports.unity.messages.controller.model;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.activity.ChatScreenAdapter;
import com.sports.unity.messages.controller.activity.ForwardSelectedItems;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.FileOnCloudHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by madmachines on 19/11/15.
 */
public class ToolbarActionsForChatScreen {

    private Context context = null;

    private SportsUnityDBHelper sportsUnityDBHelper = null;

    private boolean selectedFlag = false;
    private boolean mediaSelected = false;
    private int mediaSelectedItems = 0;
    private int selecteditems = 0;

    private boolean searchViewActivate = false;

    private ArrayList<Integer> selectedItemsList = new ArrayList<>();
    private ArrayList<Integer> positions = new ArrayList<>();


    private static ToolbarActionsForChatScreen toolbarActionsForChatScreen = null;

    public ToolbarActionsForChatScreen(Context context) {
        this.context = context;
        sportsUnityDBHelper = sportsUnityDBHelper.getInstance(this.context);

    }

    synchronized public static ToolbarActionsForChatScreen getInstance(Context context) {
        if (toolbarActionsForChatScreen == null) {
            toolbarActionsForChatScreen = new ToolbarActionsForChatScreen(context);
        }
        return toolbarActionsForChatScreen;
    }

    public void copySelectedMessages(ArrayList<Message> messageList) {
        String label = "spu";
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clip;
        String clipText = "";
        for (int i = 0; i < selectedItemsList.size(); i++) {
            String text = messageList.get(selectedItemsList.get(i)).textData;
            if (i == selectedItemsList.size() - 1) {
                clipText += text;
            } else {
                clipText += text + "\n";
            }
        }
        clip = ClipData.newPlainText(label, clipText);
        clipboard.setPrimaryClip(clip);
        if (selecteditems == 1) {
            Toast.makeText(context, R.string.copy_text_to_clipboard, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, selecteditems + " " + context.getString(R.string.copy_text_multiple_to_clipboard), Toast.LENGTH_SHORT).show();
        }
    }

    public void forwardSelectedMessages(ArrayList<Message> messageList) {
        Intent forwardIntent = new Intent(context, ForwardSelectedItems.class);
        ArrayList<Integer> idList = new ArrayList<>();
        for (int selectedItemIds :
                selectedItemsList) {
            idList.add(messageList.get(selectedItemIds).id);
        }
        forwardIntent.putIntegerArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS, idList);
        forwardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(forwardIntent);
    }

    public void deleteMessages(ArrayList<Message> messageList, long chatID, ChatScreenAdapter chatScreenAdapter) {
        HashMap<String, ArrayList<String>> mapOnType = new HashMap<>();
        mapOnType.put(SportsUnityDBHelper.MIME_TYPE_IMAGE, new ArrayList<String>());
        mapOnType.put(SportsUnityDBHelper.MIME_TYPE_VIDEO, new ArrayList<String>());
        mapOnType.put(SportsUnityDBHelper.MIME_TYPE_AUDIO, new ArrayList<String>());

        Collections.sort(selectedItemsList, Collections.reverseOrder());
        for (int itemPosition : selectedItemsList) {
            Message message = messageList.get(itemPosition);

            if (message.mediaFileName != null) {
                ArrayList<String> deletedFileNames = mapOnType.get(message.mimeType);
                deletedFileNames.add(message.mediaFileName);
            }

            sportsUnityDBHelper.deleteMessageFromTable(message.id);
            messageList.remove(message);
        }

        DBUtil.deleteContentFromExternalFileStorage(context, mapOnType);

        chatScreenAdapter.notifydataset(messageList);
        if (messageList.isEmpty()) {
            sportsUnityDBHelper.updateChatEntry(sportsUnityDBHelper.getDummyMessageRowId(), chatID, sportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
        } else {
            sportsUnityDBHelper.updateChatEntry(messageList.get(messageList.size() - 1).id, chatID, sportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
        }
    }

    public boolean onLongClickSelectView(View view, Message message, int position) {
        if (selectedFlag == false) {
            if (searchViewActivate) {
                searchViewActivate = false;
            }

            ColorDrawable drawable = new ColorDrawable(view.getResources().getColor(R.color.list_selector));
            ((FrameLayout) view).setForeground(drawable);

            if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
                selectedFlag = true;
                selecteditems++;
                selectedItemsList.add(position);
            } else {
                int status = FileOnCloudHandler.getInstance(view.getContext()).getMediaContentStatus(message);
                if (status == FileOnCloudHandler.STATUS_UPLOADED || status == FileOnCloudHandler.STATUS_DOWNLOADED || status == FileOnCloudHandler.STATUS_NONE || message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
                    selectedFlag = true;
                    selecteditems++;
                    selectedItemsList.add(position);
                    if (!message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
                        mediaSelected = true;
                        mediaSelectedItems++;
                    }
                }
            }
        } else {

            return false;
        }

        return checkSelectedItems();
    }

    public boolean onClickSelectView(View view, int position, ArrayList<Message> messageList) {
        if (selectedFlag == true) {
            Message message = messageList.get(position);
            if (selectedItemsList.contains(position)) {
                ColorDrawable drawable = new ColorDrawable(view.getResources().getColor(R.color.list_selector));
                ((FrameLayout) view).setForeground(drawable);

                selecteditems--;
                selectedItemsList.remove(Integer.valueOf(position));

                if (!message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
                    mediaSelectedItems--;
                    if (mediaSelectedItems == 0) {
                        mediaSelected = false;
                    }
                }
            } else {
                ColorDrawable drawable = new ColorDrawable(view.getResources().getColor(R.color.list_selector));
                ((FrameLayout) view).setForeground(drawable);

                if (message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
                    selectedFlag = true;
                    selecteditems++;
                    selectedItemsList.add(position);
                } else {
                    int status = FileOnCloudHandler.getInstance(view.getContext()).getMediaContentStatus(message);
                    if (status == FileOnCloudHandler.STATUS_UPLOADED || status == FileOnCloudHandler.STATUS_DOWNLOADED || status == FileOnCloudHandler.STATUS_NONE || message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
                        selectedFlag = true;
                        selecteditems++;
                        selectedItemsList.add(position);
                        if (!message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
                            mediaSelected = true;
                            mediaSelectedItems++;
                        }
                    }
                }
            }
        } else {
            return false;
        }

        return checkSelectedItems();
    }

    public boolean isItemSelected(int position) {
        boolean selected = false;
        if (selectedItemsList.contains(position)) {
            selected = true;
        } else {
            //nothing
        }
        return selected;
    }

    public void getToolbarMenu(Toolbar mtoolbar, Menu menu) {

        if (getSelectionflag()) {
            getMediaActions(mtoolbar, menu);
        } else if (getSearchFlag()) {
            Log.i("search", "true");
            getSeachControls(mtoolbar, menu);
        } else {
            mtoolbar.findViewById(R.id.selectedItems).setVisibility(View.GONE);
            MenuItem deleteMessage = menu.findItem(R.id.delete);
            deleteMessage.setVisible(false);
            MenuItem copyMessage = menu.findItem(R.id.copy);
            copyMessage.setVisible(false);
            MenuItem forwardMessage = menu.findItem(R.id.forward);
            forwardMessage.setVisible(false);
            MenuItem upNavigation = menu.findItem(R.id.navigating_up);
            upNavigation.setVisible(false);
            MenuItem downNavigation = menu.findItem(R.id.navigating_down);
            downNavigation.setVisible(false);
            mtoolbar.findViewById(R.id.search_text).setVisibility(View.GONE);
            mtoolbar.findViewById(R.id.profile).setVisibility(View.VISIBLE);
        }

    }

    private void getSeachControls(Toolbar mtoolbar, Menu menu) {
        mtoolbar.findViewById(R.id.profile).setVisibility(View.GONE);
        MenuItem deleteMessage = menu.findItem(R.id.delete);
        deleteMessage.setVisible(false);
        MenuItem copyMessage = menu.findItem(R.id.copy);
        copyMessage.setVisible(false);
        MenuItem forwardMessage = menu.findItem(R.id.forward);
        forwardMessage.setVisible(false);
        MenuItem blockUser = menu.findItem(R.id.action_block_user);
        blockUser.setVisible(false);
        MenuItem viewContact = menu.findItem(R.id.action_view_contact);
        viewContact.setVisible(false);
        MenuItem clearChat = menu.findItem(R.id.action_clear_chat);
        clearChat.setVisible(false);
        MenuItem upNavigation = menu.findItem(R.id.navigating_up);
        upNavigation.setVisible(true);
        MenuItem downNavigation = menu.findItem(R.id.navigating_down);
        downNavigation.setVisible(true);
        MenuItem searchMessages = menu.findItem(R.id.searchChatScreen);
        searchMessages.setVisible(false);
        mtoolbar.findViewById(R.id.selectedItems).setVisibility(View.GONE);
        mtoolbar.findViewById(R.id.backarrow).setVisibility(View.VISIBLE);
        EditText et = (EditText) mtoolbar.findViewById(R.id.search_text);
        et.setVisibility(View.VISIBLE);
        et.requestFocus();
    }

    private void getMediaActions(Toolbar mtoolbar, Menu menu) {
        mtoolbar.findViewById(R.id.profile).setVisibility(View.GONE);
        mtoolbar.findViewById(R.id.search_text).setVisibility(View.GONE);
        MenuItem deleteMessage = menu.findItem(R.id.delete);
        deleteMessage.setVisible(true);
        MenuItem copyMessage = menu.findItem(R.id.copy);
        copyMessage.setVisible(true);
        MenuItem forwardMessage = menu.findItem(R.id.forward);
        forwardMessage.setVisible(true);
        MenuItem searchMessages = menu.findItem(R.id.searchChatScreen);
        searchMessages.setVisible(false);
        MenuItem blockUser = menu.findItem(R.id.action_block_user);
        blockUser.setVisible(false);
        MenuItem viewContact = menu.findItem(R.id.action_view_contact);
        viewContact.setVisible(false);
        MenuItem clearChat = menu.findItem(R.id.action_clear_chat);
        clearChat.setVisible(false);
        MenuItem upNavigation = menu.findItem(R.id.navigating_up);
        upNavigation.setVisible(false);
        MenuItem downNavigation = menu.findItem(R.id.navigating_down);
        downNavigation.setVisible(false);
        TextView noOfSelectedItems = (TextView) mtoolbar.findViewById(R.id.selectedItems);
        noOfSelectedItems.setVisibility(View.VISIBLE);
        noOfSelectedItems.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
        noOfSelectedItems.setText(String.valueOf(toolbarActionsForChatScreen.getSelecteditems()));
        if (getMediaSelectionflag()) {
            copyMessage.setVisible(false);
        }
    }


    public void setSearchFlag(boolean value) {
        this.searchViewActivate = value;
    }

    public boolean getSearchFlag() {
        return searchViewActivate;
    }

    public boolean getSelectionflag() {
        return selectedFlag;
    }

    public boolean getMediaSelectionflag() {
        return mediaSelected;
    }

    public int getSelecteditems() {
        return selecteditems;
    }

    public void resetVariables() {
        selectedFlag = false;
        selecteditems = 0;
        selectedItemsList.clear();
        mediaSelected = false;
        mediaSelectedItems = 0;
    }

    public void resetList(ListView mChatView) {
        selectedFlag = false;
        selecteditems = 0;
        selectedItemsList.clear();
        mediaSelected = false;
        mediaSelectedItems = 0;


        for (int i = 0; i < mChatView.getChildCount();
             i++) {
            View v = mChatView.getChildAt(i);
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private boolean checkSelectedItems() {
        if (selecteditems == 0) {
            selectedFlag = false;
            return true;
        } else {
            if (selecteditems > 0) {
                return true;
            } else {
                //do nothing
            }
        }
        return false;
    }

}
