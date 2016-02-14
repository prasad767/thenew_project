package com.visa.vts.certificationapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.core.Session;
import com.visa.vts.certificationapp.utils.Constants;

/**
 * Created by prrathin on 11/17/15.
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    public static String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void processToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    protected void processToolbarWithoutBackNavigation() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public void showLogScreen(Bundle bundle, int responseStatus, int request, String response) {
        Intent intent = new Intent(this, LogActivity.class);
//        intent.putExtra("status", responseStatus);
//        intent.putExtra("response", response);
//        intent.putExtra("request", request);
        if (bundle != null) {
            bundle.putString("response", response);
            bundle.putInt("status", responseStatus);
            bundle.putInt("request", request);
        } else {
            bundle = new Bundle();
            bundle.putString("response", response);
            bundle.putInt("status", responseStatus);
            bundle.putInt("request", request);
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showLogScreen(int responseStatus, int request, String response) {
        showLogScreen(null, responseStatus, request, response);
    }

    public void showTnCActivity() {
        startActivity(new Intent(this, TermsAndConditionActivity.class));
    }

    public void showDashboard() {
        Session.getInstance().appFlow = Constants.FLOW_NONE;
        Intent intent = new Intent(BaseActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (Session.getInstance().appFlow == Constants.FLOW_ADD_A_CARD) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.txt_alert_discontinue_flow);
            // Add the buttons
            builder.setPositiveButton(R.string.btn_continue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setNegativeButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    showDashboard();
                    finish();
                }
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }
}
