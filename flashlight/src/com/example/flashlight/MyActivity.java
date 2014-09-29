package com.example.flashlight;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.flashlight.util.NotificationUtil;
import com.example.flashlight.util.ShortcutUtil;
import com.umeng.analytics.MobclickAgent;

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
        Log.d(this.getClass().getName(), "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.initView();

        //add shortcut
        ShortcutUtil.handleWithShortcut(this, R.drawable.icon);
    }

    /**
     * initial the view.
     */
    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setOnClickListener(this);
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
        Log.d(this.getClass().getName(), "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isLightOn && camera!= null) {
            NotificationUtil.addNotification(this, MyActivity.class, NOTIFICATION, R.drawable.icon,
                    "click into the app and close flashlight!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
