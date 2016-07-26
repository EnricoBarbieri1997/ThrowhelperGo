package com.github.enricobarbieri1997.throwhelpergo;

import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class ThrowhelperGo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_throwhelper_go);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //torna il touch :: getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // back non fa nulla, devo trovare il modod di falo passare sotto e inoltre il tasto home chiude la app
    }

    @Override
    public void onBackPressed() {
        //
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
