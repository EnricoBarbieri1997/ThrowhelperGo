package com.github.enricobarbieri1997.throwhelpergo;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.IntentCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import de.mobilej.overlay.R;

public class OverlayShowingService extends Service implements OnTouchListener, OnClickListener, View.OnLongClickListener {

    private View topLeftView;

    private ImageButton overlayedButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private DrawView aim;
    private WindowManager.LayoutParams fillParamsTouchable;
    private WindowManager.LayoutParams fillParamsNotTouchable;
    private static int ONGOING_NOTIFICATION_ID = 1;
    //We need to declare the receiver with onReceive function as below
    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };

    private boolean viewHidden = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new ImageButton(this);
        String mDrawableName = "pokeball";
        int resId = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
        overlayedButton.setImageDrawable(getResources().getDrawable(resId));
        overlayedButton.setOnTouchListener(this);
        overlayedButton.setBackgroundColor(Color.BLACK);
        overlayedButton.setOnClickListener(this);
        overlayedButton.setOnLongClickListener(this);
        overlayedButton.setBackgroundColor(Color.TRANSPARENT);
        //overlayedButton.setMaxWidth();

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        wm.addView(overlayedButton, params);

        topLeftView = new View(this);
        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        topLeftParams.gravity = Gravity.LEFT | Gravity.TOP;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        wm.addView(topLeftView, topLeftParams);

        fillParamsTouchable = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        fillParamsTouchable.gravity = Gravity.LEFT | Gravity.TOP;

        fillParamsNotTouchable = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
        fillParamsNotTouchable.gravity = Gravity.LEFT | Gravity.TOP;

        //Crea la notifica
        initNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //**Your code **
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            wm.removeView(topLeftView);
            overlayedButton = null;
            topLeftView = null;
        }
        if (aim != null) {
            aim.setVisibility(View.GONE);
            wm.removeView(aim);
            wm.removeViewImmediate(aim);
        }
        if (stopServiceReceiver != null){
            unregisterReceiver(stopServiceReceiver);
            stopServiceReceiver = null;
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    protected void onPause() {
        if (stopServiceReceiver != null){
            unregisterReceiver(stopServiceReceiver);
            stopServiceReceiver = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

           // System.out.println("topLeftY="+topLeftLocationOnScreen[1]);
          //  System.out.println("originalY="+originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            wm.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (moving) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v)
    {
        if (aim != null && !viewHidden)
        {
            wm.removeView(aim);
        }
        else
        {
            aim = new DrawView(this);

            wm.addView(aim, fillParamsNotTouchable);

            wm.removeView(overlayedButton);
            wm.addView(overlayedButton, params);
        }
        viewHidden = !viewHidden;
    }

    @Override
    public boolean onLongClick(View v)
    {
        if(aim != null && !viewHidden)
        {
            // Initialize the message
            Toast toast = Toast.makeText(getApplicationContext(), "long click", Toast.LENGTH_SHORT);

            // Gets the status of the view
            boolean touchable = aim.isTouchable();

            // Switch and update
            if (touchable)
            {
                wm.updateViewLayout(aim, fillParamsNotTouchable);
                toast.setText("Not Touchable");
            } else {
                wm.updateViewLayout(aim, fillParamsTouchable);
                toast.setText("Touchable");
            }
            aim.setTouchable(!touchable);

            // Show message
            toast.show();
        }

        return true;
    }


    // Create Notification
    private void initNotification() {
        //Register a receiver to stop Service
        registerReceiver(stopServiceReceiver, new IntentFilter("myFilter"));
        PendingIntent closingIntent = PendingIntent.getBroadcast(this, 0, new Intent("myFilter"), PendingIntent.FLAG_UPDATE_CURRENT);
        //Crea la notifica
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.pokeball)
                .setOngoing(true)
                .addAction(R.drawable.ic_close_white_18dp, getText(R.string.close_action), closingIntent)
                .setContentIntent(closingIntent)
                .build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }


}