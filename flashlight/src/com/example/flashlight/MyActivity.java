package com.example.flashlight;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MyActivity extends Activity implements View.OnClickListener {
    private Camera camera;
    private ImageView imageView;
    private boolean isLightOn = false;

    //exit
    private long mExitTime;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.initView();
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
        }
        //to intercept MENU, no operation when it is pressed.
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
