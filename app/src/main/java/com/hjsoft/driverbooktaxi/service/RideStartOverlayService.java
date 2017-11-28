package com.hjsoft.driverbooktaxi.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.hjsoft.driverbooktaxi.R;
import com.hjsoft.driverbooktaxi.activity.RideLocal;
import com.hjsoft.driverbooktaxi.activity.RideStartActivity;

/**
 * Created by hjsoft on 3/2/17.
 */
public class RideStartOverlayService extends Service implements View.OnClickListener {


    private Button overlayedButton;
    private WindowManager wm;


    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new Button(this);
        overlayedButton.setText("PCS Cabs");
        overlayedButton.setAllCaps(false);
        overlayedButton.setPadding(6,6,6,6);
        overlayedButton.setTextSize(16);
        overlayedButton.setTextColor(Color.parseColor("#000000"));
        // overlayedButton.setOnTouchListener(this);
        // overlayedButton.setAlpha(0.0f);
        //overlayedButton.setBackgroundColor(Color.BLACK);
        overlayedButton.setBackgroundResource(R.drawable.bt_bg);
        overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        wm.addView(overlayedButton, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //System.out.println("destroy getting calledddddd..............");

        // wm.removeView(overlayedButton);

    }

    @Override
    public void onClick(View view) {

        //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_LONG).show();

        wm.removeView(overlayedButton);

        Intent it = new Intent("intent.my.action");
        it.setComponent(new ComponentName(getApplicationContext().getPackageName(), RideLocal.class.getName()));
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getApplicationContext().startActivity(it);
    }
}



