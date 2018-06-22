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
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    TextView text;
    int cnt = 0, toSave = 0;
    MyApp ma;
    Location lo;
    Resources rs;
    String lt, lg, slo, slt, slg;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mMActivity;

        MyHandler(MainActivity mactivity){
            mMActivity = new WeakReference<>(mactivity);
        }

        @Override
        public void handleMessage(Message msg) {
            Location destLo = new Location(LocationManager.GPS_PROVIDER);
            float dis;
            MainActivity activity = mMActivity.get();
            if (activity == null)
                return;
            switch (msg.what){
                case 1:
                    activity.text = activity.findViewById(R.id.main);
                    activity.lo = (Location) msg.obj;
                    Log.i("MyApp", "lo in msg" + activity.lo);
                    activity.text.setText(String.format(activity.lt, activity.lo.getLatitude() + " "));
                    activity.text.append(String.format(activity.lg, activity.lo.getLongitude()) + " ");
                    destLo.setLatitude(Double.valueOf(activity.slt));
                    destLo.setLongitude(Double.valueOf(activity.slg));
                    dis = activity.lo.distanceTo(destLo);
                    activity.text.append(String.valueOf(dis));
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
        slt = slo.substring(0, slo.indexOf("Longitude"));
        slt = slt.substring(slt.indexOf(" ") + 1);
        slg = slo.substring(slo.indexOf("Longitude"));
        slg = slg.substring(slg.indexOf(" " + 1));
        text = findViewById(R.id.main);
        text.setText(slt);
        text.append(slg);
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
