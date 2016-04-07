package com.sports.unity.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.common.model.SettingsHelper;

/**
 * Created by madmachines on 25/3/16.
 */
public class AlertDialogUtil {


    public static final int ACTION_CLEAR_ALL_CHAT = 1;
    public static final int ACTION_CLEAR_ALL_MESSAGES = 2;
    public static final int ACTION_DELETE_ALL_CHAT = 3;
    public static final int ACTION_DELETE_CHAT = 4;
    public static final int ACTION_EXIT_AND_DELETE_GROUP = 5;

    private int actionId = 0;
    private String dialogTitle = null;
    private String positiveButtonTitle = null;
    private String negativeButtonTitle = null;
    private Activity activity = null;
    private DialogInterface.OnClickListener positiveButtonClickListener = null;

    public AlertDialogUtil(int actionId, String dialogTitle, String positiveButtonTitle, String negativeButtonTitle, Activity activity, DialogInterface.OnClickListener positiveButtonClickListener) {
        this.actionId = actionId;
        this.dialogTitle = dialogTitle;
        this.positiveButtonTitle = positiveButtonTitle;
        this.negativeButtonTitle = negativeButtonTitle;
        this.activity = activity;
        this.positiveButtonClickListener = positiveButtonClickListener;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(dialogTitle);

        builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing and alert dialog will dismiss
            }

        });

        builder.setPositiveButton(positiveButtonTitle, positiveButtonClickListener);

        builder.create().show();
    }
}
