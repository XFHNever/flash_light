package com.example.flashlight;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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

        if (!isAddShortcut()) {
             addShortcut();
        }
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

    /**
     * check if shortcut is installed
     *
     * @return
     */
    private boolean isAddShortcut() {
        boolean isInstallShortcut = false;

        final ContentResolver resolver = this.getContentResolver();

        String AUTHORITY = "com.android.launcher.settings";
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/favorites?notify=false");
        Cursor cursor = resolver.query(CONTENT_URI, new String[]{"title", "iconResource"},
                "title=?",new String[] { getString(R.string.app_name) }, null);

        if (cursor != null && cursor.getCount() > 0) {
            isInstallShortcut = true;
        }

        return isInstallShortcut;
    }

    /**
     * install shortcut.
     */
    private void addShortcut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //set parameters
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));

        //set the icon of shortcut
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this,R.drawable.icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,icon);

        //if allows to add repeatedly
        shortcut.putExtra("duplicate", false);

        //set the operation when clicking the shortcut
        Intent intent = new Intent(this, this.getClass());
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        //broadcast
        this.sendBroadcast(shortcut);
    }

    /**
     * remove shortcut
     */
    private void removeShortcut() {

    }
}
