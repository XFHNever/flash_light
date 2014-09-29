package com.example.flashlight;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.flashlight.util.ShortcutUtil;

public class MyActivity extends Activity implements View.OnClickListener {
    private Camera camera;
    private ImageView imageView;
    private boolean isLightOn = false;

    //exit
    private long mExitTime;

    private final static int NOTIFICATION = 123;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.initView();

        //add shortcut
        ShortcutUtil.handleWithShortcut(this);
    }

    /**
     * initial the view.
     */
    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (camera != null) {
            camera.release();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (isLightOn) {
            imageView.setImageResource(R.drawable.off);
            handler.sendEmptyMessage(1);
        } else {
            imageView.setImageResource(R.drawable.on);
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    camera = Camera.open();
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); // continue light，FLASH_MODE_ON
                    camera.setParameters(params);
                    camera.startPreview(); // start to  light
                    isLightOn = true;
                    break;
                case 1:
                    camera.stopPreview(); // close light
                    camera.release(); // close camera
                    isLightOn = false;
                    break;
            }

        }
    };

    /**
     * handle with exit event.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME){
            Log.d("Home", "HomeEvent is called");
        }

        //to intercept MENU, no operation when it is pressed.
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("flashlight")
                .setContentText("click into the app and close flashlight!")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        // Creates an explicit intent for an Activity
        Intent resultIntent = new Intent(this, MyActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MyActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION, builder.getNotification());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ddd", "MYonStop is called");

        if (isLightOn) {
            addNotification();
        }
    }
}
