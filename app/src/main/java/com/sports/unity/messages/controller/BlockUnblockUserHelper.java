package com.sports.unity.messages.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.viewhelper.ChatKeyboardHelper;

/**
 * Created by madmachines on 17/11/15.
 */
public class BlockUnblockUserHelper {

    private boolean blockStatus = false;
    private Activity activity = null;

    public BlockUnblockUserHelper(boolean blockStatus, Activity activity) {
        this.blockStatus = blockStatus;
        this.activity = activity;
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

    public void showAlert_ToSendMessage_UnblockUser(Activity activity, long contactId, String phoneNumber, Menu menu) {
        alert_Dialog("To send message, please unblock this user.", false, activity, contactId, phoneNumber, menu);
    }

    public void onMenuItemSelected(Activity activity, long contactId, String phoneNumber, Menu menu) {
        if (blockStatus) {
            startTaskToBlockOrUnBlock(false, activity, contactId, phoneNumber, menu);
        } else {
            alert_Dialog("Are you really want to block this user ?", true, activity, contactId, phoneNumber, menu);
        }
    }

    private void alert_Dialog(String message, final boolean status, final Activity activity, final long contactId, final String phoneNumber, final Menu menu) {
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

    private void startTaskToBlockOrUnBlock(boolean status, Activity activity, long contactId, String phoneNumber, Menu menu) {
        new BlockOrUnBlockUserAsyncTask(status, activity, contactId, phoneNumber, menu).execute();
    }

    private void postActionOnBlockOrUnblock(boolean status, Menu menu) {
        MenuItem item = menu.findItem(R.id.action_block_user);

        blockStatus = status;
        ChatKeyboardHelper.getInstance(false).disableKeyboardAndMediaButtons(blockStatus, activity);
        if (blockStatus == true) {
            item.setTitle("Unblock User");
        } else {
            item.setTitle("Block User");
        }
    }

    private class BlockOrUnBlockUserAsyncTask extends AsyncTask<Void, Void, Void> {

        private boolean status = false;
        private Activity activity;
        private long contactId;
        private String phoneNumber;
        private Menu menu;

        private ProgressDialog progressDialog = null;

        private boolean success = false;

        public BlockOrUnBlockUserAsyncTask(boolean status, Activity activity, long contactId, String phoneNumber, Menu menu) {
            this.status = status;
            this.activity = activity;
            this.contactId = contactId;
            this.phoneNumber = phoneNumber;
            this.menu = menu;
        }

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(activity, "", "Please wait a moment...", true);

        }

        protected Void doInBackground(Void... params) {
            success = PersonalMessaging.getInstance(activity.getApplicationContext()).
                    changeUserBlockStatus(phoneNumber, status);

            if (success == true) {
                SportsUnityDBHelper.getInstance(activity.getApplicationContext()).
                        updateUserBlockStatus(contactId, status);
            } else {
                //nothing
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (success == true) {
                postActionOnBlockOrUnblock(status, menu);
            } else {
                Toast.makeText(activity.getApplicationContext(), "failed !!", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }

    }

}
