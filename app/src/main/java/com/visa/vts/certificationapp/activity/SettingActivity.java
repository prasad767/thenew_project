package com.visa.vts.certificationapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;

import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.ContextHelper;
import com.visa.vts.certificationapp.utils.Logger;

/**
 * Created by prrathin on 11/17/15.
 */
public class SettingActivity extends BaseActivity {
    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mySwitch = (Switch) findViewById(R.id.setting_log_switch);
        mySwitch.setChecked(Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG));
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Preference.getInstance().saveBoolean(Constants.PREFERENCE_ENABLELOG, isChecked);
            }
        });
        Button sendLog = (Button) findViewById(R.id.sendlogs);
        sendLog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendEmail();
                // Intent intent = new Intent(SettingActivity.this,Sendlogfiles.class);
                // startActivity(intent);


            }
        });
/*
        Button selectEnv = (Button) findViewById(R.id.select_environment);
        selectEnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEnv = new Intent(SettingActivity.this,EnvironmentActivity.class);
                startActivity(intentEnv);
            }
        });
        */
    }

    private void sendEmail() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + "recipient@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "email's subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "email's body");
        emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://sdcard/Files/Project_Name/visa.txt"));
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SettingActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
}
