package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    int mCnt = 0, mToSave = 0;
    String mLatitude, mLongitude, mSavedLocation, mSavedLatitude, mSavedLongitude;
    MyApp mMyApp;
    TextView mTextView;
    Location mLocation;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mMActivity;

        MyHandler(MainActivity mactivity) {
            mMActivity = new WeakReference<>(mactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            Location destLo = new Location(LocationManager.GPS_PROVIDER);
            float dis;
            MainActivity activity = mMActivity.get();
            if (activity == null) {
                Log.w("MyApp", "No activity associated with handler.");
                return;
            }
            switch (msg.what) {
                case 1:
                    activity.mTextView = activity.findViewById(R.id.main);
                    activity.mLocation = (Location) msg.obj;
                    Log.i("MyApp", "mLocation in msg" + activity.mLocation);
                    activity.mTextView.setText(String.format(activity.mLatitude, activity.mLocation.getLatitude() + " "));
                    activity.mTextView.append(String.format(activity.mLongitude, activity.mLocation.getLongitude()) + " ");
                    destLo.setLatitude(Double.valueOf(activity.mSavedLatitude));
                    destLo.setLongitude(Double.valueOf(activity.mSavedLongitude));
                    dis = activity.mLocation.distanceTo(destLo);
                    activity.mTextView.append(String.valueOf(dis));
                    activity.mCnt++;
                    if (activity.mToSave > 0)
                        activity.mToSave--;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitude = getString(R.string.latitude);
        mLongitude = getString(R.string.longitude);
        mMyApp = (MyApp) getApplication();
        mMyApp.setHandler(mHandler);
        mSharedPreferences = getSharedPreferences("saved-loc", Context.MODE_PRIVATE);

        mSavedLocation = mSharedPreferences.getString("location", "");
        Log.i(getString(R.string.LogTagMain), "saved location: " + mSavedLocation);

        if (mSavedLocation.equals("")) {
            Log.i(getString(R.string.LogTagMain), "No saved location yet.");
        } else {
            mSavedLatitude = mSavedLocation.substring(0, mSavedLocation.indexOf("Longitude"));
            mSavedLatitude = mSavedLatitude.substring(mSavedLatitude.indexOf(" ") + 1);
            mSavedLongitude = mSavedLocation.substring(mSavedLocation.indexOf("Longitude"));
            mSavedLongitude = mSavedLongitude.substring(mSavedLongitude.indexOf(" " + 1));
            mTextView = findViewById(R.id.main);
            mTextView.setText(mSavedLatitude);
            mTextView.append(mSavedLongitude);
        }
    }

    public void calculateBKCode() {
        int a, b;
        Integer tmp;
        EditText inCode = findViewById(R.id.editText);
        TextView outCode = findViewById(R.id.VerCode);
        String inStr = inCode.getText().toString(), verification;

        if (inStr.length() != 16) {
            outCode.setText(String.format(getString(R.string.errlen), inStr.length()));
            return;
        }

        a = Character.digit(inStr.charAt(3), 10);
        b = Character.digit(inStr.charAt(8), 10);
        tmp = ((a == 0 ? 1 : a) - b + 9) % 10;
        verification = tmp.toString();

        a = Character.digit(inStr.charAt(3), 10);
        tmp = (10 - a) % 10;
        verification += tmp;

        verification += inStr.charAt(0);
        verification += inStr.charAt(8);

        a = Character.digit(inStr.charAt(6), 10);
        b = Character.digit(inStr.charAt(8), 10);
        tmp = a * 10 + b;

        a = Character.digit(inStr.charAt(3), 10);
        tmp = tmp - ((tmp - ((a == 0) ? 1 : a) + 1) / 10 + 1);
        verification += (tmp < 10) ? ('0' + tmp.toString()) : tmp.toString();

        verification += inStr.charAt(6);
        verification += inStr.charAt(3);

        outCode.setText(verification);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                mSharedPreferences = getSharedPreferences("saved-loc", Context.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.putString("location", mTextView.getText().toString());
                mEditor.apply();
                break;
            case R.id.verify:
                calculateBKCode();
                break;
        }
    }
}
