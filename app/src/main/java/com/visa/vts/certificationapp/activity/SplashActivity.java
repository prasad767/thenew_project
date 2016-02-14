package com.visa.vts.certificationapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.storage.Preference;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //initialize the Preference with context
        Preference.getInstance(this);
        showNextScreen();
    }


    public void showNextScreen() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                startActivity(new Intent(SplashActivity.this, EnvironmentActivity.class));
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, getResources().getInteger(R.integer.splash_timer));

    }
}
