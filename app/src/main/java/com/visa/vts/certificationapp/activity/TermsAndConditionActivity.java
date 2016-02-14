package com.visa.vts.certificationapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.visa.cbp.external.common.RiskData;
import com.visa.cbp.external.common.TermsAndConditions;
import com.visa.cbp.external.enp.ProvisionRequestWithEnrollId;
import com.visa.cbp.external.enp.ProvisionResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.manager.NetworkManager;
import com.visa.vts.certificationapp.model.request.RiskParams;
import com.visa.vts.certificationapp.network.BackendEndPoints;
import com.visa.vts.certificationapp.network.RestController;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.AlertDialogSingleButton;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.EncryptionUtility;
import com.visa.vts.certificationapp.utils.JWTFactory;
import com.visa.vts.certificationapp.utils.JWTLocalUtility;
import com.visa.vts.certificationapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class TermsAndConditionActivity extends BaseActivity {
    private VisaPaymentSDK mVisaPaymentSDK;
    private VTSCertApp mMainApp;
    private String mTermsAndConditionId, mResponseBody, mPanEnrollmentID,
            mVProvisionedTokenID, mProvisionAPI, mCvv, mFullName, mBillingZip, mAddressLine1;
    private ArrayList<String> mUnSupportedPresentationTypes = new ArrayList<String>();
    private Activity mContext;
    private int mResponseStatus, mSC;
    private Bundle mBundle;
    private Button mProvisionButton;
    private com.visa.vts.certificationapp.utils.ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        processToolbar();

        getBundleValue();

        classAndWidgetInitialize();

        setTnCButton();

        processToolbar();

    }

    /**
     * Initialize class And Widget
     */
    private void classAndWidgetInitialize() {
        mMainApp = (VTSCertApp) getApplicationContext();
        mVisaPaymentSDK = mMainApp.getVisaPaymentSDK();
        mContext = TermsAndConditionActivity.this;
        mProgressDialog = new com.visa.vts.certificationapp.utils.ProgressDialog(mContext);

        mProvisionButton = (Button) findViewById(R.id.btnProvisioning);

        clickListener();
    }


    /**
     * get Bundle Value
     */
    private void getBundleValue() {

        mBundle = getIntent().getExtras();

        mTermsAndConditionId = mBundle.getString(Constants.TERMSCONDITIONID);
        mPanEnrollmentID = mBundle.getString(Constants.PANENROLLMENTID);
        Utils.printLog("onCreate mPanEnrollmentID: " + mPanEnrollmentID);
        Utils.printLog("onCreate mTermsAndConditionId: " + mTermsAndConditionId);
        mUnSupportedPresentationTypes = (ArrayList<String>) mBundle.getSerializable(Constants.UNSUPPORTED_PRESENTATIONTYPES);
        mCvv = mBundle.getString(Constants.CVV);
        mBillingZip = mBundle.getString(Constants.BILLING_ZIP);
        mAddressLine1 = mBundle.getString(Constants.ADDRESS_LINE_1);
        mFullName = mBundle.getString(Constants.ACCOUNT_HOLDER_NAME);


//        mTermsAndConditionId = getIntent().getStringExtra(Constants.TERMSCONDITIONID);
//        mPanEnrollmentID = getIntent().getStringExtra(Constants.PANENROLLMENTID);
//        Utils.printLog("onCreate mPanEnrollmentID: " + mPanEnrollmentID);
//        Utils.printLog("onCreate mTermsAndConditionId: " + mTermsAndConditionId);
//        mUnSupportedPresentationTypes = (ArrayList<String>)getIntent().getSerializableExtra(Constants.UNSUPPORTED_PRESENTATIONTYPES);
//        mCvv = getIntent().getStringExtra(Constants.CVV);
//        mBillingZip = getIntent().getStringExtra(Constants.BILLING_ZIP);
//        mAddressLine1 = getIntent().getStringExtra(Constants.ADDRESS_LINE_1);
//        mFullName = getIntent().getStringExtra(Constants.ACCOUNT_HOLDER_NAME);
    }

    /**
     * click Listener
     */
    private void clickListener() {
        mProvisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provision();
            }
        });
    }

    /**
     * set TnC Button according to the log status
     */
    private void setTnCButton() {
        if (!Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
            mProvisionButton.setText("Accept");
        }
    }

    /**
     * provision function call
     */
    private void provision() {
        //ShowProgressBar();
        //String serverNonce = "99218e2656f042d1b4f2553e6c69e95f0389";
        //EnrollDeviceResponse enrollDeviceResponse = new EnrollDeviceResponse();
        //enrollDeviceResponse.setVServerNonce(serverNonce);
        //mVisaPaymentSDK.processEnrollDeviceresponse(enrollDeviceResponse);

        if (!NetworkManager.checkInternet(mContext))
            return;

        mProgressDialog.show();

        mProgressDialog.setOnDismissListener(new com.visa.vts.certificationapp.utils.ProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                navigateToNextPage();
            }
        });

        final ProvisionRequestWithEnrollId provisionPan = getProvisionTokenRequest();
        BackendEndPoints service = RestController.getApi();
        Call<ProvisionResponse> call = service.provision(provisionPan, mPanEnrollmentID);
        call.enqueue(new Callback<ProvisionResponse>() {
            @Override
            public void onResponse(final Response<ProvisionResponse> response, Retrofit retrofit) {
                Utils.printLog("Response Code " + response.code());
                mResponseStatus = response.code();

                mResponseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);
                Utils.printLog("Response Body " + mResponseBody);

                if (response.body() != null) {
                    mVProvisionedTokenID = response.body().getVProvisionedTokenID();

                    Utils.printLog("mVProvisionedTokenID for test before saving :) " + mVProvisionedTokenID);
                    Log.d("vTokenID saving", "" + mVProvisionedTokenID);
                    mMainApp.getPreference().saveString(Constants.vProvisionedTokenID, mVProvisionedTokenID);


                    mProvisionAPI = response.body().getTokenInfo().getHceData().getDynParams().getApi();
                    mSC = response.body().getTokenInfo().getHceData().getDynParams().getSc();


                    String tokenInfo = response.body().getTokenInfo().getHceData().getDynParams().getEncKeyInfo();
                    String tokenSplit[] = tokenInfo.split("\\.");
                    String tokendecoded = new String(EncryptionUtility.bs64Decode(tokenSplit[1]));
                    String decryptedLUK = JWTLocalUtility.decryptJwe(getApplicationContext(), new JWTLocalUtility.JWEData(tokendecoded), R.raw.issuer_enc_priv_key);
//                String decryptedLUK = JWTUtility.decryptJwe(new JWTUtility.JWEData(tokendecoded),
                    //                       VTSCertApp.encryptprivKeyPEM);
                    Log.d("TnCAc:tokendecoded-", tokendecoded);
                    Log.d("TnCAc:decryptedLUK-", decryptedLUK);
                /*
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String serverNonce = "99218e2656f042d1b4f2553e6c69e95f0389";
                        EnrollDeviceResponse enrollDeviceResponse = new EnrollDeviceResponse();
                        enrollDeviceResponse.setVServerNonce(serverNonce);
                        mVisaPaymentSDK.processEnrollDeviceresponse(enrollDeviceResponse);
                        Log.d("visaPayment",serverNonce);
                        try {
                            tokenKey = mVisaPaymentSDK.storeProvisionedToken(response.body(), mPanEnrollmentID);
                            mVisaPaymentSDK.selectCard(tokenKey);

                           //   mMainApp.getPreference().saveString(Constants.TOKENKEY, tokenKey);
                        } catch (TokenInvalidException e) {
                            e.printStackTrace();
                        }

                    }
                }));
*/
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable cause) {
                cause.getLocalizedMessage();
                Log.v("TAG", "failure");
                mProgressDialog.dismiss();
            }
        });

    }

    /**
     * get Provision TokenRequest
     *
     * @return ProvisionRequestWithEnrollId
     */
    private ProvisionRequestWithEnrollId getProvisionTokenRequest() {
        ProvisionRequestWithEnrollId provisionRequestWithEnrollId = new ProvisionRequestWithEnrollId();

        Utils.printLog("mPanEnrollmentID: " + mPanEnrollmentID);
        Utils.printLog("mTermsAndConditionId: " + mTermsAndConditionId);
        provisionRequestWithEnrollId.setvPanEnrollmentID(mPanEnrollmentID);
        provisionRequestWithEnrollId.setClientAppID(Constants.SANDBOX_EXTERNAL_APPID);
        //provisionRequestWithEnrollId.setClientDeviceID(mVisaPaymentSDK.getCerts().getDeviceId());
        //try {
        provisionRequestWithEnrollId.setClientDeviceID(Constants.clientDeviceID);
        // provisionRequestWithEnrollId.setClientDeviceID(mVisaPaymentSDK.getDeviceId());
        // } catch (DeviceIDValidationException e) {
        //    e.printStackTrace();
        // }
        provisionRequestWithEnrollId.setClientWalletAccountEmailAddress(Constants.walletAccountEmailId);
        provisionRequestWithEnrollId.setClientWalletAccountEmailAddressHash(Constants.walletAccountHash);
        provisionRequestWithEnrollId.setClientWalletAccountID(Constants.walletAccountID);
        provisionRequestWithEnrollId.setIp4address(Constants.IPV4ADDRESS);
        provisionRequestWithEnrollId.setLocation(Constants.LOCATION);
        provisionRequestWithEnrollId.setProtectionType("SOFTWARE");


        //set encRiskData
        provisionRequestWithEnrollId.setEncRiskDataInfo(JWTFactory.createJwtFromEnvironment(getApplicationContext(), getRiskData()));

        //presentation type
        List<String> presentationtype = new ArrayList<String>();
        presentationtype.add(Constants.PRESENTATIONTYPE_NFC);
        presentationtype.add(Constants.PRESENTATIONTYPE_MST);
        presentationtype.add(Constants.PRESENTATIONTYPE_INAPP);
/*
        for (String presentationTypeString : presentationtype)
        {
            for (String unsupportedPresentationTypeString : mUnSupportedPresentationTypes)
            {
                if (presentationTypeString.matches(unsupportedPresentationTypeString))
                {
                    presentationtype.remove(unsupportedPresentationTypeString);
                }
            }
        }
        */
        provisionRequestWithEnrollId.setPresentationType(presentationtype);

        //terms and condition
        TermsAndConditions termsAndConditions = new TermsAndConditions();
        termsAndConditions.setId(mTermsAndConditionId);
        termsAndConditions.setDate(Long.toString(System.currentTimeMillis()));

        //terms and condition
        provisionRequestWithEnrollId.setTermsAndConditions(termsAndConditions);

        return provisionRequestWithEnrollId;
    }

    private String getRiskData() {
        RiskParams riskParams = new RiskParams(mCvv, mBillingZip, mAddressLine1, mFullName);
        List<RiskData> mapList = riskParams.initializeRiskParams();
        Gson gson = new Gson();
        String riskDataInClear = gson.toJson(mapList);

        Utils.printLog("Before Encrypting RiskData: " + riskDataInClear);

        return riskDataInClear;
    }

    /**
     * navigate To NextPage
     * this function will decide whether log page should show or
     * take the user to next screen
     */
    private void navigateToNextPage() {
        //  mBundle.putString(Constants.TOKENKEY, new Gson().toJson(tokenKey));
        mBundle.putString(Constants.VProvisionedTokenID, mVProvisionedTokenID);
        mBundle.putString(Constants.mProvisionAPI, mProvisionAPI);
        mBundle.putInt(Constants.mSC, mSC);


        if (Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
            showLogScreen(mBundle, mResponseStatus, Constants.REQUEST_PROVISIONING, mResponseBody);
        } else {
            if (mResponseStatus == Constants.STATUS_SUCCESS || mResponseStatus == Constants.STATUS_SUCCESS1) {
                Intent intent = new Intent(this, ConfirmAddCardActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            } else {
                AlertDialogSingleButton.showAlert(mContext, "Provision failed", new AlertDialogSingleButton.setOnNeutralButtonClick() {
                    @Override
                    public void onNeutralButton(DialogInterface dialog) {

                    }
                });
            }
        }
    }
}
