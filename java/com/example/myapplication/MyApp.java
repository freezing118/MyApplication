package com.example.myapplication;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyApp extends Application{
    AudioManager mAudioManager;
    Location mLocation = null;
    LocationManager mLocationManager = null;
    Timer mTimer = new Timer();
    Handler mHandler;
    int mVolMax;

    TimerTask volumeTask = new TimerTask() {
        @Override
        public void run() {
            int vol, permission;
            Message msg;
            vol = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            Log.d(getString(R.string.LogTagMain), "volume:" + vol + "/" + mVolMax);

            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mVolMax/4, 0);
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(getString(R.string.LogTagMain), "GPS enabled");
            }
            permission = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    Process.myPid(), Process.myUid());
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.e(getString(R.string.LogTagMain), "No permission to" +
                        " access fine location!");
                return;
            }
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mLocation == null) {
                Log.w(getString(R.string.LogTagMain), "Last location get to know null");
                return;
            }
            Log.d(getString(R.string.LogTagMain), "Last location is: "
                    + mLocation.getLatitude() + ", " + mLocation.getLongitude());

            if (mHandler == null) {
                Log.w(getString(R.string.LogTagMain), "mHandler is null");
                return;
            }
            msg = mHandler.obtainMessage();
            msg.what = 1;
            msg.obj = mLocation;
            mHandler.sendMessage(msg);
        }
    };

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(getString(R.string.LogTagMain), "location changed " + location);
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
        mHandler = hl;
    }

    Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onCreate() {
        int permission;
        super.onCreate();
        Log.i(getString(R.string.LogTagMain),
                getString(R.string.AppName) + " is starting ...");

        permission = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                Process.myPid(), Process.myUid());
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e(getString(R.string.LogTagMain), "No permission to access fine location!");
            return;
        }
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager == null) {
            Log.e(getString(R.string.LogTagMain), "mLocationManager is null");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 1, mLocationListener);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (mAudioManager == null) {
            Log.e(getString(R.string.LogTagMain), "mAudioManager is null");
            return;
        }
        mVolMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        mTimer.schedule(volumeTask, 0, 10000);
    }
}
