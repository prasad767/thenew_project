package com.visa.vts.certificationapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.visa.cbp.external.common.RiskData;
import com.visa.cbp.external.common.TermsAndConditions;
import com.visa.cbp.external.enp.ProvisionAckRequest;
import com.visa.cbp.external.enp.ProvisionRequestWithEnrollId;
import com.visa.cbp.external.enp.ProvisionResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;
import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.manager.NetworkManager;
import com.visa.vts.certificationapp.model.AccountData;
import com.visa.vts.certificationapp.model.AccountDetails;
import com.visa.vts.certificationapp.model.request.RiskParams;
import com.visa.vts.certificationapp.network.BackendEndPoints;
import com.visa.vts.certificationapp.network.RestController;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.AlertDialogSingleButton;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.JWTFactory;
import com.visa.vts.certificationapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ConfirmAddCardActivity extends BaseActivity {
    private VisaPaymentSDK mVisaPaymentSDK;
    private VTSCertApp mMainApp;
    private TokenKey mTokenKey;
    private Activity mContext;
    private int mResponseStatus, mSc;
    private String mCvv2, mFullName, mBillingZip, mVProvisionedTokenID, mAddressLine1,
            mResponseBody, mGuid, mTermsAndConditionId, mExpiry, ml4dCardNumber,
            mProvisionAPI, mVPanEnrollmentID;
    private ArrayList<String> unsupportedPresentationTypes = new ArrayList<String>();
    private com.visa.vts.certificationapp.utils.ProgressDialog mProgressDialog;
    private TextView mCardDetails;
    private Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_add_card);

        if (Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
            processToolbarWithoutBackNavigation();
        } else {
            processToolbar();
        }

        getBundleValue();

        classAndWidgetInitialize();


    }

    /**
     * Initialize class And Widget
     */
    private void classAndWidgetInitialize() {

        mContext = ConfirmAddCardActivity.this;
        mMainApp = (VTSCertApp) getApplicationContext();
        mVisaPaymentSDK = mMainApp.getVisaPaymentSDK();
        mVPanEnrollmentID = Preference.getInstance().retrieveString(Constants.vPanEnrollmentID);
        mProgressDialog = new com.visa.vts.certificationapp.utils.ProgressDialog(mContext);

        mCardDetails = (TextView) findViewById(R.id.textViewCardDetails);
        mDoneButton = (Button) findViewById(R.id.btn_confirm_provisioning);

        clickListener();

        setCardDetails();
    }

    /**
     * get Bundle Value
     */
    private void getBundleValue() {

        Bundle aBundle = getIntent().getExtras();
//     mTokenKey = mMainApp.getPreference().retrieveString(Constants.TOKENKEY);

        mGuid = Preference.getInstance().retrieveString(Constants.guid);
        mProvisionAPI = aBundle.getString(Constants.mProvisionAPI);
        mSc = aBundle.getInt(Constants.mSC);
        mTokenKey = new Gson().fromJson(aBundle.getString(Constants.TOKENKEY), TokenKey.class);

        mVProvisionedTokenID = aBundle.getString(Constants.VProvisionedTokenID);
        Preference.getInstance().saveString(Constants.vProvisionedTokenID, mVProvisionedTokenID);

        Utils.printLog("mVProvisionedTokenID--->>> " + mVProvisionedTokenID);
        Log.d("GUID--->>> ", "" + mGuid);

        // mVProvisionedTokenID = mMainApp.getPreference().retrieveString(Constants.mVProvisionedTokenID);
        mTermsAndConditionId = aBundle.getString(Constants.TERMSCONDITIONID);
        mCvv2 = aBundle.getString(Constants.CVV);
        mBillingZip = aBundle.getString(Constants.BILLING_ZIP);
        mAddressLine1 = aBundle.getString(Constants.ADDRESS_LINE_1);
        mFullName = aBundle.getString(Constants.ACCOUNT_HOLDER_NAME);
//        unsupportedPresentationTypes = (ArrayList<String>) aBundle.getSerializable(Constants.UNSUPPORTED_PRESENTATIONTYPES);


//        mTokenKey = getIntent().getParcelableExtra(Constants.TOKENKEY);
//        mVProvisionedTokenID = getIntent().getStringExtra(Constants.VProvisionedTokenID);
//        mTermsAndConditionId = getIntent().getStringExtra(Constants.TERMSCONDITIONID);
//        mCvv2 = getIntent().getStringExtra(Constants.CVV);
//        mBillingZip = getIntent().getStringExtra(Constants.BILLING_ZIP);
//        mAddressLine1 = getIntent().getStringExtra(Constants.ADDRESS_LINE_1);
//        mFullName = getIntent().getStringExtra(Constants.ACCOUNT_HOLDER_NAME);
//        unsupportedPresentationTypes = (ArrayList<String>) getIntent().getSerializableExtra(Constants.UNSUPPORTED_PRESENTATIONTYPES);

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        String cardnumber = prefs.getString("cardnumber", null);
        ml4dCardNumber = cardnumber.substring(cardnumber.length() - 4);
        mExpiry = prefs.getString("expiry", null);

    }

    /**
     * set Card Details
     */
    private void setCardDetails() {
        mCardDetails.setText("Visa ..." + ml4dCardNumber + "," + "Exp " + mExpiry);
    }

    /**
     * click Listener
     */
    private void clickListener() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(ConfirmAddCardActivity.this, DashboardActivity.class));
                finish();*/
                try {
                    confirmProvision();
                } catch (TokenInvalidException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * function to call confirm Provision
     *
     * @throws TokenInvalidException
     */
    private void confirmProvision() throws TokenInvalidException {

        if (!NetworkManager.checkInternet(mContext))
            return;

        mProgressDialog.show();

        mProgressDialog.setOnDismissListener(new com.visa.vts.certificationapp.utils.ProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                navigateToNextPage();
            }
        });
        ProvisionAckRequest provisionAckRequest = new ProvisionAckRequest();

        provisionAckRequest.setApi(mProvisionAPI);
        Utils.printLog("provisionAckRequest.setApi " + provisionAckRequest.getApi());
        //provisionAckRequest.setApi("06096206");
        provisionAckRequest.setProvisioningStatus("SUCCESS");
        // provisionAckRequest = mVisaPaymentSDK.constructProvisionAck(mTokenKey);

        /*
            try {
                    if(mVisaPaymentSDK != null){
                        //provisionAckRequest = mVisaPaymentSDK.constructProvisionAck(mTokenKey);
                        provisionAckRequest = mVisaPaymentSDK.

                    }
                } catch (TokenInvalidException e) {
                    e.printStackTrace();
                }
                //This function is not working in OCT SDK.Also obsereved there is nothing called confirm provisioning in OCT SDK
        */

        BackendEndPoints service = RestController.getApi();
        Call<ProvisionResponse> call = service.confirmProvision(provisionAckRequest, mVProvisionedTokenID);

        //Call<ProvisionResponse> call = service.confirmProvision(provisionAckRequest);//, mVProvisionedTokenID);
        call.enqueue(new Callback<ProvisionResponse>() {
            @Override
            public void onResponse(Response<ProvisionResponse> response, Retrofit retrofit) {
                Utils.printLog("Response Code " + response.code());
                mResponseStatus = response.code();

                mResponseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable throwable) {
                mProgressDialog.dismiss();
            }
        });

    }

    /**
     * add CardList in preference
     *
     * @param accountdetails
     */
    private void addCardToList(AccountDetails accountdetails) {
        AccountData accountData = AccountData.getInstance();
        accountData.addAccount(accountdetails);
        Log.v("ConfirmAddCardActivity", "GetAccount = " + accountData.getNoAccounts());
    }

    @Override
    public void onBackPressed() {

        if (Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
        } else {
            super.onBackPressed();

        }
    }

    /**
     * navigate To NextPage
     * this function will decide whether log page should show or
     * take the user to next screen
     */
    private void navigateToNextPage() {

        if (mResponseStatus == Constants.STATUS_SUCCESS || mResponseStatus == Constants.STATUS_SUCCESS1) {
            AccountDetails aAccountDetails = new AccountDetails(ml4dCardNumber, mExpiry, AccountDetails.AccountType.CREDIT_CARD, mVProvisionedTokenID, false);
            aAccountDetails.setSc(mSc);
            aAccountDetails.setProvisionAPI(mProvisionAPI);
            aAccountDetails.setMguid(mGuid);
            aAccountDetails.setvPanEnrollmentID(mVPanEnrollmentID);
            addCardToList(aAccountDetails);

            Utils.printLog("ProvisionAPI : " + aAccountDetails.getProvisionAPI() + "VProvisionTokenId : " + aAccountDetails.getvProvisionedTokenID() + "SC : " + aAccountDetails.getSc());
        }

        if (Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
            showLogScreen(mResponseStatus, Constants.REQUEST_CONFIRM_PROVISIONING, mResponseBody);
        } else {

            if (mResponseStatus == Constants.STATUS_SUCCESS || mResponseStatus == Constants.STATUS_SUCCESS1) {
                showDashboard();
            } else {
                AlertDialogSingleButton.showAlert(mContext, "Confirm Provision failed", new AlertDialogSingleButton.setOnNeutralButtonClick() {
                    @Override
                    public void onNeutralButton(DialogInterface dialog) {

                    }
                });
            }

        }
    }


    private ProvisionRequestWithEnrollId provisionTokenRequest() {

        ProvisionRequestWithEnrollId provisionRequestWithEnrollId = new ProvisionRequestWithEnrollId();
//        provisionRequestWithEnrollId.setvPanEnrollmentID(panEnrollmentID);
        provisionRequestWithEnrollId.setClientAppID(Constants.SANDBOX_EXTERNAL_APPID);
        provisionRequestWithEnrollId.setClientDeviceID(mVisaPaymentSDK.getCerts().getDeviceId());
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
        for (String presentationTypeString : presentationtype) {
            for (String unsupportedPresentationTypeString : unsupportedPresentationTypes) {
                if (presentationTypeString.matches(unsupportedPresentationTypeString)) {
                    presentationtype.remove(unsupportedPresentationTypeString);
                }
            }
        }*/
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
        RiskParams riskParams = new RiskParams(mCvv2, mBillingZip, mAddressLine1, mFullName);
        List<RiskData> mapList = riskParams.initializeRiskParams();
        Gson gson = new Gson();
        String riskDataInClear = gson.toJson(mapList);
        return riskDataInClear;
    }

}
