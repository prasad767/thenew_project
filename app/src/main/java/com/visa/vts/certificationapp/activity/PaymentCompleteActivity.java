package com.visa.vts.certificationapp.activity;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.visa.vts.certificationapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class PaymentCompleteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_complete);
        showCardListScreen();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Payment is in progress",Toast.LENGTH_SHORT).show();
    }

    public void showCardListScreen(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //startActivity(new Intent(PaymentCompleteActivity.this, CardListActivity.class));
                finish();
            }
        };

        Timer timer =new Timer();
        timer.schedule(timerTask, getResources().getInteger(R.integer.payment_complete_timer));

    }
}
