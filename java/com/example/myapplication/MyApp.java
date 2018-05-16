package com.example.myapplication;

//import android.Manifest;
import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

//import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MyApp extends Application{
    AudioManager am;
    Location lo = null;
    LocationManager lm = null;
    Timer volumeTimer = new Timer();
    Handler mhl;
    String slo;
    SharedPreferences sp;

    TimerTask volumeTask = new TimerTask() {
        @Override
        public void run() {
            int vol, volMax, permission;
            Message msg;
            vol = am.getStreamVolume(AudioManager.STREAM_RING);
            volMax = am.getStreamMaxVolume(AudioManager.STREAM_RING);
            Log.i("MyApp", "volume:" + vol + "max:" + volMax);
            am.setStreamVolume(AudioManager.STREAM_RING, 3, 0);
            permission = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    android.os.Process.myPid(), android.os.Process.myUid());
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lo = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.i("MyApp", "loc" + lo.getLatitude() + ", " + lo.getLongitude());
            if (mhl != null) {
                msg = mhl.obtainMessage();
                msg.what = 1;
                msg.obj = lo;
                mhl.sendMessage(msg);
            }
        }
    };

    LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("MyApp", "location changed " + location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    void setHandler(Handler hl) {
        mhl = hl;
    }

    Handler getHandler() {
        return mhl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyApp", "in myapp");

        sp = getSharedPreferences("saved-loc", Context.MODE_PRIVATE);
        slo = sp.getString("location", "");
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeTimer.schedule(volumeTask, 0, 10000);
    }
}
