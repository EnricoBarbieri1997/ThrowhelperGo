package com.github.enricobarbieri1997.throwhelpergo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;

public class ThrowhelperGo extends Activity {
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.M){

        } else{
            if (Settings.canDrawOverlays(this)) {
                Log.i("Throw helper go", "Starting settings");
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            }
        }

        Intent svc = new Intent(this, OverlayShowingService.class);
        startService(svc);
        finish();
    }
}