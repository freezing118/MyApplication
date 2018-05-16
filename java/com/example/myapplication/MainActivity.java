package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    TextView text;
    int cnt = 0, toSave = 0;
    MyApp ma;
    Location lo;
    Resources rs;
    String str;

    public static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mMActivity;

        MyHandler(MainActivity mactivity){
            mMActivity = new WeakReference<>(mactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mMActivity.get();
            if (activity == null)
                return;
            switch (msg.what){
                case 1:
                    activity.text = activity.findViewById(R.id.main);
                    activity.lo = (Location) msg.obj;
                    activity.text.setText(String.format(activity.str, msg.obj));
                    activity.cnt++;
                    if (activity.toSave > 0)
                        activity.toSave--;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    MyHandler hl = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rs = getApplicationContext().getResources();
        str = rs.getString(R.string.location);
        ma = (MyApp) getApplication();
        ma.setHandler(hl);
    }
}
