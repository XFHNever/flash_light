package com.example.flashlight.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import com.example.flashlight.R;

/**
 * Tool to deal with Shortcut.
 *
 * Created by fuxie on 2014/9/29  10:47.
 */
public class ShortcutUtil {

    /**
     * public interface of installing shortcut.
     *
     * @param activity
     */
    public static void handleWithShortcut(Activity activity, int iconId) {
        if (!isAddShortcut(activity)) {
            addShortcut(activity, iconId);
        }
    }

    /**
     * check if shortcut is installed
     *
     * @return
     */
    private static boolean isAddShortcut(Activity activity) {
        boolean isInstallShortcut = false;

        final ContentResolver resolver = activity.getContentResolver();

        String AUTHORITY = "com.android.launcher2.settings";

        int version  = android.os.Build.VERSION.SDK_INT;
        if (version < 8) {
            AUTHORITY = "com.android.launcher.settings";
        }
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/favorites?notify=true");
        Cursor cursor = resolver.query(CONTENT_URI, new String[]{"title", "iconResource"},
                "title=?",new String[] { activity.getString(R.string.app_name) }, null);

        if (cursor != null && cursor.getCount() > 0) {
            isInstallShortcut = true;
        }

        return isInstallShortcut;
    }

    /**
     * install shortcut.
     */
    private static void addShortcut(Activity activity, int iconId) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //set parameters
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(R.string.app_name));

        //set the icon of shortcut
        Parcelable icon = Intent.ShortcutIconResource.fromContext(activity, iconId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,icon);

        //if allows to add repeatedly
        shortcut.putExtra("duplicate", false);

        //set the operation when clicking the shortcut
        Intent intent = new Intent(activity, activity.getClass());
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        //broadcast
        activity.sendBroadcast(shortcut);
    }

    /**
     * remove shortcut
     */
    private void removeShortcut() {

    }
}
