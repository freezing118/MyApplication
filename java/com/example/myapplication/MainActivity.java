package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    TextView text;
    int cnt = 0, toSave = 0;
    MyApp ma;
    Location lo;
    Resources rs;
    String lt, lg, slo;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

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
                    Log.i("MyApp", "lo in msg" + activity.lo);
                    activity.text.setText(String.format(activity.lt, activity.lo.getLatitude() + " "));
                    activity.text.append(String.format(activity.lg, activity.lo.getLongitude()));
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
        lt = rs.getString(R.string.latitude);
        lg = rs.getString(R.string.longitude);
        ma = (MyApp) getApplication();
        ma.setHandler(hl);
        sp = getSharedPreferences("saved-loc", Context.MODE_PRIVATE);
        slo = sp.getString("location", "");
        text = findViewById(R.id.main);
        text.setText(slo.substring(0, slo.indexOf("Longitude")));
        text.append(slo.substring(slo.indexOf("Longitude")));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button:
                sp = getSharedPreferences("saved-loc", Context.MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("location", text.getText().toString());
                editor.apply();
                break;
        }
    }
}
