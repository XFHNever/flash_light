package com.example.flashlight.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.example.flashlight.R;

/**
 * Created by fuxie on 2014/9/29  16:30.
 */
public class NotificationUtil {
    public static void addNotification(Activity activity,  Class<?> cls, int id, int iconId, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                .setSmallIcon(iconId)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText(content)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        // Creates an explicit intent for an Activity
        Intent resultIntent = new Intent(activity, cls);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0,
                resultIntent, 0);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, builder.getNotification());
    }

}
