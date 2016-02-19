package com.sports.unity.common.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sports.unity.R;

import java.util.ArrayList;

/**
 * utility class for handling the runtime
 * permissions introduced in API level 6.0 .
 */
public class PermissionUtil {
    private static PermissionUtil permissionUtil;

    /**
     * blank constructor .
     */
    private PermissionUtil() {
    }

    /**
     * method to instantiate the {@link PermissionUtil} class.
     *
     * @return single instance of {@link PermissionUtil} .
     */
    public static PermissionUtil getInstance() {
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtil();
        }
        return permissionUtil;
    }

    /**
     * checks if the permission is already granted.
     * if not then requests user to allow the
     * permissions.
     * <p>It is possible that user already denied the requested permissions in that
     * case displays an explanation message for requested permissions.</p>
     *
     * @param activity    Origin activity to launch from.
     * @param permission  the ArrayList of permissions to be requested.
     * @param message     explanation message for permission to user.
     * @param requestCode If >= 0, this code will be returned in
     *                    {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
     * @return whether requested permission is already granted.
     */
    public boolean requestPermission(Activity activity, final ArrayList<String> permission, String message, int requestCode) {
        boolean isAlreadyGranted = true;
        for (String s : permission) {
            if (ActivityCompat.checkSelfPermission(activity, s)
                    != PackageManager.PERMISSION_GRANTED) {
                isAlreadyGranted = false;
            }
        }
        if (!isAlreadyGranted) {
            promptPermissionDialog(activity, permission, message, requestCode);
        }
        return isAlreadyGranted;
    }

    public boolean requestPermission(Activity activity, final ArrayList<String> permission) {
        boolean isAlreadyGranted = true;
        for (String s : permission) {
            if (ActivityCompat.checkSelfPermission(activity, s)
                    != PackageManager.PERMISSION_GRANTED) {
                isAlreadyGranted = false;
            }
        }
        return isAlreadyGranted;
    }

    /**
     * checks if the requested permission is already granted or not
     *
     * @param context    Application context.
     * @param permission requested permission.
     * @return
     */
    public boolean isPermissionGranted(Context context, String permission) {
        boolean isAlreadyGranted = true;
        if (ActivityCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            isAlreadyGranted = false;
        }
        return isAlreadyGranted;
    }

    /**
     * invokes if the requested permission is not already granted by the user.
     *
     * @param activity    Origin activity to launch from.
     * @param permission  the ArrayList of permissions to be requested.
     * @param message     explanation message for permission to user.
     * @param requestCode If >= 0, this code will be returned in
     *                    {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
     */
    private void promptPermissionDialog(final Activity activity, final ArrayList<String> permission, String message, final int requestCode) {
        boolean isGranted = false;
        for (String s : permission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    s)) {
                isGranted = true;
            }
        }
        if (isGranted) {
            final Snackbar sb = Snackbar.make(activity.findViewById(android.R.id.content), message,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(activity,
                                    permission.toArray(new String[permission.size()]), requestCode);
                        }
                    });
            View v = sb.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
            params.gravity = Gravity.TOP;
            v.setLayoutParams(params);
            sb.setActionTextColor(activity.getResources().getColor(R.color.app_theme_blue));
            TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
            sb.show();
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sb.dismiss();
                }
            });
        } else {
            ActivityCompat.requestPermissions(activity,
                    permission.toArray(new String[permission.size()]),
                    requestCode);
        }
    }

    /**
     * verifies that all the requested permissions are granted.
     *
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     * @return whether the requested permissions are granted.
     */

    public boolean verifyPermissions(int[] grantResults) {

        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * display permission denial message to user
     *
     * @param activity origin activity.
     * @param message  denial message.
     */
    public void showSnackBar(final Activity activity, String message) {
        Snackbar sb = Snackbar.make(activity.findViewById(android.R.id.content), message,
                Snackbar.LENGTH_LONG);
        View v = sb.getView();
        sb.setActionTextColor(activity.getResources().getColor(R.color.app_theme_blue));
        TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        sb.show();
    }

    /**
     * checks whether the runtime permission handling is required.
     *
     * @return whether target devices API level is 6.0 .
     */
    public boolean isRuntimePermissionRequired() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        } else {
            return true;
        }
    }
}
