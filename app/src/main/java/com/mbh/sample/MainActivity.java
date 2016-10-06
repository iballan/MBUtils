package com.mbh.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mbh.mbutils.thread.MBThreadUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MBThreadUtils.DoOnBackground(new Runnable() {
            @Override
            public void run() {
                Log.i("TEST", "TEST ON BACKGROUND");
            }
        });
    }
}
