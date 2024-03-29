package com.sports.unity.messages.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.viewhelper.ChatKeyboardHelper;

/**
 * Created by madmachines on 17/11/15.
 */
public class BlockUnblockUserHelper {

    private boolean blockStatus = false;
    private Activity activity = null;
    private TextView lastSeenText;
    private BlockUnblockListener blockUnblockListener;

    public BlockUnblockUserHelper(boolean blockStatus, Activity activity, TextView lastSeenText) {
        this.blockStatus = blockStatus;
        this.activity = activity;
        this.lastSeenText = lastSeenText;
    }

    public boolean isBlockStatus() {
        return blockStatus;
    }

    public void initViewBasedOnBlockStatus(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_block_user);
        if (blockStatus) {
            menuItem.setTitle("Unblock User");
        } else {
            menuItem.setTitle("Block User");
        }
    }

    public void showAlert_ToSendMessage_UnblockUser(Activity activity, int contactId, String phoneNumber, Menu menu) {
        alert_Dialog("To send message, please unblock this user.", false, activity, contactId, phoneNumber, menu);
    }

    public void onMenuItemSelected(Activity activity, int contactId, String phoneNumber, Menu menu) {
        if (blockStatus) {
            startTaskToBlockOrUnBlock(false, activity, contactId, phoneNumber, menu);
        } else {
            alert_Dialog("Are you really want to block this user ?", true, activity, contactId, phoneNumber, menu);
        }
    }

    private void alert_Dialog(String message, final boolean status, final Activity activity, final int contactId, final String phoneNumber, final Menu menu) {
        AlertDialog.Builder altDialog = new AlertDialog.Builder(activity);
        altDialog.setMessage(message);
        altDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTaskToBlockOrUnBlock(status, activity, contactId, phoneNumber, menu);
            }
        });
        altDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }

        );
        altDialog.show();
    }

    private void startTaskToBlockOrUnBlock(boolean status, Activity activity, int contactId, String phoneNumber, Menu menu) {
        new BlockOrUnBlockUserAsyncTask(status, activity, contactId, phoneNumber, menu).execute();
    }

    private void postActionOnBlockOrUnblock(boolean status, Menu menu, String phoneNumber) {
        MenuItem item = null;
        blockStatus = status;

        if (null != menu) {
            ChatKeyboardHelper.getInstance(false).disableOrEnableKeyboardAndMediaButtons(blockStatus, activity);
            item = menu.findItem(R.id.action_block_user);
        }
        if (blockStatus == true) {
            if (null != blockUnblockListener) {
                blockUnblockListener.onBlock(true, phoneNumber);
            }
            if (item != null) {
                item.setTitle("Unblock User");
                if (lastSeenText != null) {
                    lastSeenText.setVisibility(View.GONE);
                }
            }
        } else {
            if (null != blockUnblockListener) {
                blockUnblockListener.onUnblock(true);
            }
            if (item != null) {
                item.setTitle("Block User");
                if (lastSeenText != null) {
                    lastSeenText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void updateBlockStatus(boolean blockStatus) {
        this.blockStatus = blockStatus;
    }

    private class BlockOrUnBlockUserAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean status = false;
        private Activity activity;
        private int contactId;
        private String phoneNumber;
        private Menu menu;

        private ProgressDialog progressDialog = null;

        private boolean success = false;

        public BlockOrUnBlockUserAsyncTask(boolean status, Activity activity, int contactId, String phoneNumber, Menu menu) {
            this.status = status;
            this.activity = activity;
            this.contactId = contactId;
            this.phoneNumber = phoneNumber;
            this.menu = menu;
        }

        protected void onPreExecute() {
            ProgressBar progressBar = new ProgressBar(activity);
            progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

            progressDialog = ProgressDialog.show(activity, "", "Please wait a moment...", true);
            progressDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());

        }

        protected Void doInBackground(Void... params) {
            success = PersonalMessaging.getInstance(activity.getApplicationContext()).
                    changeUserBlockStatus(phoneNumber, status);

            if (success == true) {
                SportsUnityDBHelper.getInstance(activity.getApplicationContext()).
                        updateUserBlockStatus(contactId, status);
                checkIfRequestStatusNotDefaultThenUpdateRequestStatus(phoneNumber);
            } else {
                //nothing
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (success == true) {
                postActionOnBlockOrUnblock(status, menu, phoneNumber);
            } else {
                if (null != blockUnblockListener) {
                    if (blockStatus) {
                        blockUnblockListener.onUnblock(false);
                    } else {
                        blockUnblockListener.onBlock(false, phoneNumber);
                    }
                }
                Toast.makeText(activity.getApplicationContext(), "failed !!", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }

    }

    private void checkIfRequestStatusNotDefaultThenUpdateRequestStatus(String phoneNumber) {
        int requestId = SportsUnityDBHelper.getInstance(activity).checkJidForPendingRequest(phoneNumber);
        if (requestId == Contacts.REQUEST_BLOCKED) {
            SportsUnityDBHelper.getInstance(activity).updateContactFriendRequestStatus(phoneNumber, Contacts.PENDING_REQUESTS_TO_PROCESS);
        } else {
            // do nothing
        }
    }

    public void addBlockUnblockListener(BlockUnblockListener listener) {
        blockUnblockListener = listener;
    }

    public void removeBlockUnblockListener() {
        blockUnblockListener = null;
    }

    public interface BlockUnblockListener {

        public void onBlock(boolean success, String phoneNumber);

        public void onUnblock(boolean success);
    }

}
