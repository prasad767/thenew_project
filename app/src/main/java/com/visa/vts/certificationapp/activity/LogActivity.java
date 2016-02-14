package com.visa.vts.certificationapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.utils.Constants;

/**
 * Created by prrathin on 12/2/15.
 */
public class LogActivity extends BaseActivity {
    private static final String TAG = LogActivity.class.getCanonicalName();
    private int request, status;
    private String message, response;
    private Bundle aBundle = null;
    private Button mNextButton;
    private TextView mResponseTextView, mResponseStatusTextView;
    private ImageView mStatusImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log);

        getBundleValue();

        classAndWidgetInitialize();

        processToolCheckLog();

        setButtonText();

        setResponse();

        setButtonStatus();

    }
    /**
     * Initialize class And Widget
     */
    private void classAndWidgetInitialize() {
        mStatusImageView = (ImageView) findViewById(R.id.log_status_icon);
        mNextButton = (Button) findViewById(R.id.log_btn_next);
        mResponseTextView = (TextView) findViewById(R.id.log_response);
        mResponseStatusTextView = (TextView) findViewById(R.id.log_response_status);

    }
    /**
     * Set Response
     * set the response code and response message which we get from bundle
     */
    private void setResponse() {
        mResponseTextView.setText(getString(R.string.response_body) + response);
        mResponseStatusTextView.setText(getString(R.string.response_status_code) + status);
    }

    /**
     * Set Button text
     * if the screen is confirm provision or LCM screen the button should named as Done
     * else it remains Next
     */
    private void setButtonText() {

        if (request == Constants.REQUEST_CONFIRM_PROVISIONING || request == Constants.REQUEST_LCM) {
            mNextButton.setText(R.string.btn_done);
        }
    }

    /**
     * get Value from bundle
     */
    private void getBundleValue() {

        aBundle = getIntent().getExtras();
        status = aBundle.getInt("status");
        response = aBundle.getString("response");
        request = aBundle.getInt("request");
    }



    /**
     * set Button Status
     * Button should be enabled only if the response is success and the screen is confirm provision or LCM screen
     * i.e response code should be 200 and 201
     */
    private void setButtonStatus() {

        if (status == Constants.STATUS_SUCCESS || status == Constants.STATUS_SUCCESS1) {
            mStatusImageView.setBackgroundResource(R.drawable.ic_check_circle_green);
            mNextButton.setEnabled(true);
        } else {
            mStatusImageView.setBackgroundResource(R.drawable.ic_cancel_red);
            mNextButton.setEnabled(false);
        }

        if (request == Constants.REQUEST_CONFIRM_PROVISIONING || request == Constants.REQUEST_LCM) {
            mNextButton.setText(R.string.btn_done);
            mNextButton.setEnabled(true);
        }

    }

    private void processToolCheckLog() {
        if (request == Constants.REQUEST_ENROLL_DEVICE) {
            processToolbarWithoutBackNavigation();
        } else if (request == Constants.REQUEST_ENROLL_PAN) {
            processToolbarWithoutBackNavigation();
        } else if (request == Constants.REQUEST_PROVISIONING) {
            processToolbarWithoutBackNavigation();
        } else if (request == Constants.REQUEST_CONFIRM_PROVISIONING) {
            processToolbarWithoutBackNavigation();
        } else if (request == Constants.REQUEST_LCM) {
            processToolbarWithoutBackNavigation();
        }
    }

    /**
     * on Next Button Click
     *
     * @param view
     */
    public void onNextClicked(View view) {
        Intent intent = null;
        if (request == Constants.REQUEST_ENROLL_DEVICE) {
            showDashboard();
        } else if (request == Constants.REQUEST_ENROLL_PAN) {
            intent = new Intent(this, TermsAndConditionActivity.class);
        } else if (request == Constants.REQUEST_PROVISIONING) {
            intent = new Intent(this, ConfirmAddCardActivity.class);
        } else if (request == Constants.REQUEST_CONFIRM_PROVISIONING) {
            showDashboard();
        } else if (request == Constants.REQUEST_LCM) {
            finish();

        }
        if (intent != null && aBundle != null) {
            intent.putExtras(aBundle);
        }
        if (intent != null) {
            startActivity(intent);
        }
        finish();

    }

    /**
     * on Email Button Click
     *
     * @param view
     */
    public void onEmailLogClicked(View view) {
        sendEmail();
    }

    /**
     * Function to send mail the response
     */
    private void sendEmail() {

        if (request == Constants.REQUEST_ENROLL_DEVICE) {
            message = "Sample backend json response...";
        } else if (request == Constants.REQUEST_ENROLL_PAN) {
            message = "Terms and conditions accepted!!  Proceed for confirmation...";
        } else if (request == Constants.REQUEST_PROVISIONING) {
            message = "Sample provisioning json response...";
        } else if (request == Constants.REQUEST_CONFIRM_PROVISIONING) {
            message = "Sample confirm provisioning response...";
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + "recipient@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "email's subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LogActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        if ((request == Constants.REQUEST_PROVISIONING) || (request == Constants.REQUEST_CONFIRM_PROVISIONING)) {
        } else {
            super.onBackPressed();

        }
    }
}
