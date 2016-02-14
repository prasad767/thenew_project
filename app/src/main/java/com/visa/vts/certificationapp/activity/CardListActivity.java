package com.visa.vts.certificationapp.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.visa.cbp.external.aam.ReplenishAckRequest;
import com.visa.cbp.external.aam.ReplenishRequest;
import com.visa.cbp.external.aam.ReplenishResponse;
import com.visa.cbp.external.aam.Signature;
import com.visa.cbp.external.common.DynParams;
import com.visa.cbp.external.common.HceData;
import com.visa.cbp.external.common.TokenInfo;
import com.visa.cbp.external.common.UpdateReason;
import com.visa.cbp.external.lcm.LcmTokenRequest;
import com.visa.cbp.external.lcm.LcmTokenResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.data.LcmParams;
import com.visa.cbp.sdk.facade.data.TokenKey;
import com.visa.cbp.sdk.facade.exception.TokenInvalidException;
import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.adapter.CardListAdapter;
import com.visa.vts.certificationapp.manager.NetworkManager;
import com.visa.vts.certificationapp.model.AccountData;
import com.visa.vts.certificationapp.model.AccountDetails;
import com.visa.vts.certificationapp.network.BackendEndPoints;
import com.visa.vts.certificationapp.network.RestController;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.AlertDialogDoubleButton;
import com.visa.vts.certificationapp.utils.AlertDialogSingleButton;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CardListActivity extends BaseActivity {

    private CardListAdapter cardListAdapter;
    private LcmParams lcmParams;
    private Activity mContext;
    private RecyclerView mRecyclerView;
    private LinearLayout myEmptyLayout;
    private LinearLayoutManager mLayoutManager;
    private VisaPaymentSDK visaPaymentSDK;
    private VTSCertApp mainApp;
    private TokenKey tokenKey;
    private int selectedCardIndex = 0, mSc, responseStatus, mItemId;
    private String vProvisionedTokenID, mProvisionAPI, responseBody;
    private com.visa.vts.certificationapp.utils.ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        processToolbar();

        mainApp = (VTSCertApp) getApplicationContext();
        visaPaymentSDK = mainApp.getVisaPaymentSDK();
        mContext = CardListActivity.this;
        myEmptyLayout = (LinearLayout) findViewById(R.id.card_list_empty_LAY);

        mProgressDialog = new com.visa.vts.certificationapp.utils.ProgressDialog(mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.card_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        cardListAdapter = new CardListAdapter(this);
        mRecyclerView.setAdapter(cardListAdapter);
        cardListAdapter.notifyDataSetChanged();

        if (cardListAdapter.getItemCount() == 0) {
            myEmptyLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            myEmptyLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    public boolean onContextItemSelected(MenuItem item) {

        mItemId = item.getItemId();

        Utils.printLog("Item id : " + mItemId);
        selectedCardIndex = cardListAdapter.mselectedCardIndex;
        AccountDetails account = AccountData.getInstance().getAccountDetailList().get(selectedCardIndex);
        String VProvisionTokenId = account.getvProvisionedTokenID();
        setVProvisionTokenId(VProvisionTokenId);
        setProvisionAPI(account.getProvisionAPI());
        setSc(account.getSc());

        TokenKey token = account.getTokenKey();

        Utils.printLog("ProvisionAPI : " + getProvisionAPI() + "VProvisionTokenId : " + getVProvisionTokenId() + "SC : " + getSc());

        switch (mItemId) {
            case Constants.CMD_SUSPEND:
                Utils.printLog("Item id:" + mItemId + "Selected Card Position suspend: " + selectedCardIndex);

                AlertDialogDoubleButton.showAlert(mContext, "Do you want to suspend?", new AlertDialogDoubleButton.setOnPositiveButtonClick() {
                    @Override
                    public void onPositiveButton(DialogInterface dialog) {
                        if (!NetworkManager.checkInternet(mContext))
                            return;
                        suspend();
                    }
                }, new AlertDialogDoubleButton.setOnNegativeButtonClick() {
                    @Override
                    public void onNegativeButton(DialogInterface dialog) {

                    }
                });

                break;

            case Constants.CMD_RESUME:
                Utils.printLog("Item id:" + mItemId + "Selected Card Position resume: " + selectedCardIndex);

                AlertDialogDoubleButton.showAlert(mContext, "Do you want to resume?", new AlertDialogDoubleButton.setOnPositiveButtonClick() {
                    @Override
                    public void onPositiveButton(DialogInterface dialog) {
                        if (!NetworkManager.checkInternet(mContext))
                            return;
                        resume();
                    }
                }, new AlertDialogDoubleButton.setOnNegativeButtonClick() {
                    @Override
                    public void onNegativeButton(DialogInterface dialog) {

                    }
                });


                break;

            case Constants.CMD_DELETE:
                Utils.printLog("Item id:" + mItemId + "Selected Card Position delete: " + selectedCardIndex);

                AlertDialogDoubleButton.showAlert(mContext, "Do you want to delete?", new AlertDialogDoubleButton.setOnPositiveButtonClick() {
                    @Override
                    public void onPositiveButton(DialogInterface dialog) {
                        if (!NetworkManager.checkInternet(mContext))
                            return;
                        delete();
                    }
                }, new AlertDialogDoubleButton.setOnNegativeButtonClick() {
                    @Override
                    public void onNegativeButton(DialogInterface dialog) {

                    }
                });

                break;

            case Constants.CMD_REPLENISH:
                Utils.printLog("Item id:" + mItemId + "Selected Card Position replenish: " + selectedCardIndex);

                AlertDialogDoubleButton.showAlert(mContext, "Do you want to confirm replenish?", new AlertDialogDoubleButton.setOnPositiveButtonClick() {
                    @Override
                    public void onPositiveButton(DialogInterface dialog) {
                        if (!NetworkManager.checkInternet(mContext))
                            return;
                        replenish();
                    }
                }, new AlertDialogDoubleButton.setOnNegativeButtonClick() {
                    @Override
                    public void onNegativeButton(DialogInterface dialog) {

                    }
                });

                break;

            case Constants.CMD_CONFIRM_REPLENISH:
                AlertDialogDoubleButton.showAlert(mContext, "Do you want to confirm replenish?", new AlertDialogDoubleButton.setOnPositiveButtonClick() {
                    @Override
                    public void onPositiveButton(DialogInterface dialog) {
                        if (!NetworkManager.checkInternet(mContext))
                            return;
                        //replenish();
                        //[TODO] - Temporary calling confirm replenish directly.
                        confirmReplenish(getReplenishAckRequest(), getVProvisionTokenId());

                    }
                }, new AlertDialogDoubleButton.setOnNegativeButtonClick() {
                    @Override
                    public void onNegativeButton(DialogInterface dialog) {

                    }
                });
                break;

            case Constants.CMD_SETAS_DEFAULT:

                AlertDialogDoubleButton.showAlert(mContext, "Do you want to set as default?", new AlertDialogDoubleButton.setOnPositiveButtonClick() {
                    @Override
                    public void onPositiveButton(DialogInterface dialog) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.DEFAULT_CARD_MESSAGE, selectedCardIndex);
                        setResult(Constants.CARD_ACTION_REQUEST_CODE, intent);
                        Utils.showToast(mContext, "Set as default");
                    }
                }, new AlertDialogDoubleButton.setOnNegativeButtonClick() {
                    @Override
                    public void onNegativeButton(DialogInterface dialog) {

                    }
                });


                break;


        }

        return super.onContextItemSelected(item);
    }

    private void replenish() {

        showProgress();
        ReplenishRequest aReplenishRequest = getReplenishRequest();

        if (aReplenishRequest == null) {
            dismissProgress();
            return;
        }

        BackendEndPoints service = RestController.getApi();

        try {
            Gson gson = new Gson();
            String aPanRequest = gson.toJson(aReplenishRequest, ReplenishRequest.class);
            Utils.printLog("ReplenishRequest :" + aPanRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ReplenishResponse> call = service.Replenish(aReplenishRequest, getVProvisionTokenId());
        call.enqueue(new Callback<ReplenishResponse>() {
            @Override
            public void onResponse(Response<ReplenishResponse> response, Retrofit retrofit) {
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);

                Utils.printLog("Response Code " + response.code());
                if (response.body() != null) {
                    Utils.printLog("Response body " + response.body().toString());
                }

                dismissProgress();

            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();

            }
        });

    }

    private ReplenishRequest getReplenishRequest() {

        ReplenishRequest aReplenishRequest = new ReplenishRequest();
        Signature aSignature = new Signature();
        aSignature.setMac(String.valueOf(Integer.parseInt(getProvisionAPI()) + getSc()));
        aReplenishRequest.setSignature(aSignature);

        aReplenishRequest.setEncryptionMetaData(null);


        TokenInfo aTokenInfo = new TokenInfo();
        HceData aHceData = new HceData();
        DynParams aDynParams = new DynParams();

        aDynParams.setApi(getProvisionAPI());
        aDynParams.setSc(getSc());


        aDynParams.setTvls(getTvls());

        aHceData.setDynParams(aDynParams);
        aTokenInfo.setHceData(aHceData);

        aReplenishRequest.setTokenInfo(aTokenInfo);

        return aReplenishRequest;
    }

    private List<String> getTvls() {
        List<String> aList = new ArrayList<>();
        aList.add(EnvironmentActivity.getCurrentTimeStamp() + "||0|I");
        return aList;
    }

//    private void replenish() {
//        Utils.printLog("replenish api call");
//        List<TokenData> tokenDatas = null;
//        if (visaPaymentSDK != null) {
//            tokenDatas = visaPaymentSDK.getAllTokenData();
//        }
//        if (tokenDatas != null && tokenDatas.size() > 0) {
//            tokenKey = visaPaymentSDK.getSelectedCard();
//            if (tokenKey != null) {
//                showProgress();
//                String vProvisionTokenID = visaPaymentSDK.getTokenData(tokenKey).getVProvisionedTokenID();
//
//                ReplenishRequest replenishRequest = null;
//                try {
//                    replenishRequest = visaPaymentSDK.constructReplenishRequest(tokenKey);
//                } catch (TokenInvalidException e) {
//                    e.printStackTrace();
//                }
//                replenishRequest.setEncryptionMetaData(null);
//                replenishExecute(replenishRequest, vProvisionTokenID);
//
//            } else {
//                Toast.makeText(getApplicationContext(), "No Token exist in the device", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void replenishExecute(final ReplenishRequest replenishRequest, final String vProvisionTokenID) {

        BackendEndPoints service = RestController.getApi();
        Call<ReplenishResponse> call = service.Replenish(replenishRequest, getVProvisionTokenId());
        call.enqueue(new Callback<ReplenishResponse>() {
            @Override
            public void onResponse(Response<ReplenishResponse> response, Retrofit retrofit) {
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);

                dismissProgress();
                ReplenishAckRequest replenishAckRequest = null;
                try {
                    replenishAckRequest = visaPaymentSDK.constructReplenishAcknowledgementRequest(tokenKey);
                } catch (TokenInvalidException e) {
                    e.printStackTrace();
                }
                confirmReplenish(replenishAckRequest, vProvisionTokenID);

            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();

            }
        });

    }


    private void confirmReplenish(final ReplenishAckRequest replenishAckRequest, final String vProvisionTokenID) {
        Utils.printLog("confirmReplenish api call");
        showProgress();
        BackendEndPoints service = RestController.getApi();
        Call<ReplenishResponse> call = service.confirmReplenish(replenishAckRequest, getVProvisionTokenId());
        call.enqueue(new Callback<ReplenishResponse>() {
            @Override
            public void onResponse(Response<ReplenishResponse> response, Retrofit retrofit) {
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);
                Utils.printLog("Response Code " + response.code());
                if (response.body() != null) {
                    Utils.printLog("Response body " + response.body().toString());
                }
                dismissProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();
            }
        });
    }


    private void suspend() {
        Utils.printLog("suspend api call");
        showProgress();
        LcmTokenRequest aLcmTokenRequest = getLcmTokenRequest("FRAUD", "Unknown transactions");
        if (aLcmTokenRequest == null) {
            dismissProgress();
            return;
        }
        BackendEndPoints service = RestController.getApi();
        Call<LcmTokenResponse> call = service.lcmSuspend(aLcmTokenRequest, getVProvisionTokenId());
        call.enqueue(new Callback<LcmTokenResponse>() {
            @Override
            public void onResponse(Response<LcmTokenResponse> response, Retrofit retrofit) {
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);
                Utils.printLog("Response Code " + response.code());
                if (response.body() != null) {
                    Utils.printLog("Response body " + response.body().toString());
                }
                dismissProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();
            }
        });

    }

    private void resume() {
        Utils.printLog("resume api call");
        showProgress();
        LcmTokenRequest aLcmTokenRequest = getLcmTokenRequest("CUSTOMER_CONFIRMED", "Customer called");

        if (aLcmTokenRequest == null) {
            dismissProgress();
            return;
        }

        BackendEndPoints service = RestController.getApi();
        Call<LcmTokenResponse> call = service.lcmResume(aLcmTokenRequest, getVProvisionTokenId());
        call.enqueue(new Callback<LcmTokenResponse>() {
            @Override
            public void onResponse(Response<LcmTokenResponse> response, Retrofit retrofit) {
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);

                Utils.printLog("Response Code " + response.code());
                if (response.body() != null) {
                    Utils.printLog("Response body " + response.body().toString());
                }
                dismissProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();

            }
        });


    }

    private void delete() {
        Utils.printLog("delete api call");
        showProgress();
        LcmTokenRequest aLcmTokenRequest = getLcmTokenRequest("FRAUD", "Unknown transactions");
        if (aLcmTokenRequest == null) {

            dismissProgress();
            return;
        }

        BackendEndPoints service = RestController.getApi();
        Call<LcmTokenResponse> call = service.lcmDelete(aLcmTokenRequest, getVProvisionTokenId());
        call.enqueue(new Callback<LcmTokenResponse>() {
            @Override
            public void onResponse(Response<LcmTokenResponse> response, Retrofit retrofit) {
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);

                Utils.printLog("Response Code " + response.code());
                if (response.body() != null) {
                    Utils.printLog("Response body " + response.body().toString());
                }
                dismissProgress();
                AccountData account = AccountData.getInstance();
                account.deleteAccount(selectedCardIndex);
                cardListAdapter.notifyDataSetChanged();
                cardListAdapter.notifyItemRemoved(selectedCardIndex);
            }

            @Override
            public void onFailure(Throwable throwable) {
                dismissProgress();

            }
        });


    }

    private LcmTokenRequest getLcmTokenRequest(String aReasonCode, String ReasonDesc) {
        LcmTokenRequest lcmTokenRequest = new LcmTokenRequest();
        UpdateReason updateReason = new UpdateReason();
        //  lcmParams = new LcmParams();
        updateReason.setReasonCode(aReasonCode);
        updateReason.setReasonDesc(ReasonDesc);
        lcmTokenRequest.setUpdateReason(updateReason);

        return lcmTokenRequest;

    }

    /*
        private LcmTokenRequest getLcmTokenRequest(String aReasonCode, String ReasonDesc) {

            LcmTokenRequest lcmTokenRequest = null;
            lcmParams = new LcmParams();
            List<TokenData> tokenDatas = null;

            if (visaPaymentSDK != null) {
                tokenDatas = visaPaymentSDK.getAllTokenData();
            }

            if (tokenDatas != null && tokenDatas.size() > 0) {
    //            String vProvisionTokenID = null;
                final TokenKey tokenKey = visaPaymentSDK.getSelectedCard();
                if (tokenKey != null) {
    //                vProvisionTokenID = visaPaymentSDK.getTokenData(tokenKey).getVProvisionedTokenID();
    //                Utils.printLog("vProvisionTokenID: " + vProvisionTokenID);
                    lcmParams.setTokenKey(tokenKey);
                    lcmParams.setReasonCode(aReasonCode);
                    lcmParams.setReasonDesc(ReasonDesc);
                    // lcm(vProvisionTokenID,"Suspend");

                    try {
                        lcmTokenRequest = visaPaymentSDK.constructLcmRequest(lcmParams);
                    } catch (TokenInvalidException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "TokenKey is null", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "No Token exist in the device", Toast.LENGTH_SHORT).show();

            }
            return lcmTokenRequest;
        }
    */
    /*
    private String getVProvisionTokenId() {
        String vProvisionTokenID = null;
        try {
            final TokenKey tokenKey = visaPaymentSDK.getSelectedCard();
            if (tokenKey != null) {
                vProvisionTokenID = visaPaymentSDK.getTokenData(tokenKey).getVProvisionedTokenID();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vProvisionTokenID;
    }
    */

    private ReplenishAckRequest getReplenishAckRequest() {
        ReplenishAckRequest replenishAckRequest = new ReplenishAckRequest();
        TokenInfo aTokenInfo = new TokenInfo();
        HceData aHceData = new HceData();
        DynParams aDynParams = new DynParams();
        if (getProvisionAPI() != null)
            if (getProvisionAPI().length() > 0)
                aDynParams.setApi(String.valueOf(Integer.parseInt(getProvisionAPI()) + 1));
        aDynParams.setSc(getSc() + 1);
        aHceData.setDynParams(aDynParams);
        aTokenInfo.setHceData(aHceData);
        replenishAckRequest.setTokenInfo(aTokenInfo);
        return replenishAckRequest;
    }

    public String getProvisionAPI() {
        return mProvisionAPI;
    }

    public void setProvisionAPI(String mProvisionAPI) {
        this.mProvisionAPI = mProvisionAPI;
    }

    public String getVProvisionTokenId() {
        return vProvisionedTokenID;
    }

    public void setVProvisionTokenId(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public int getSc() {
        return mSc;
    }

    public void setSc(int mSc) {
        this.mSc = mSc;
    }


    private void showProgress() {

        if (mProgressDialog != null)
            mProgressDialog.show();
    }


    private void dismissProgress() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        mProgressDialog.setOnDismissListener(new com.visa.vts.certificationapp.utils.ProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                navigateToNextPage();
            }
        });
    }

    /**
     * navigate To NextPage
     * this function will decide whether log page should show or
     * take the user to next screen
     */
    private void navigateToNextPage() {
        if (Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
            showLogScreen(responseStatus, Constants.REQUEST_LCM, responseBody);
        } else {

            if (responseStatus == Constants.STATUS_SUCCESS || responseStatus == Constants.STATUS_SUCCESS1) {
                finish();
            } else {

                String aFailureMsg = "";

                switch (mItemId) {
                    case Constants.CMD_SUSPEND:
                        aFailureMsg = "Card suspend failed";
                        break;

                    case Constants.CMD_RESUME:
                        aFailureMsg = "Card resume failed";
                        break;

                    case Constants.CMD_DELETE:
                        aFailureMsg = "Card delete failed";
                        break;

                    case Constants.CMD_REPLENISH:
                        aFailureMsg = "Card replenish failed";
                        break;

                    case Constants.CMD_CONFIRM_REPLENISH:
                        aFailureMsg = "Card confirm replenish failed";
                        break;
                }

                AlertDialogSingleButton.showAlert(mContext, aFailureMsg, new AlertDialogSingleButton.setOnNeutralButtonClick() {
                    @Override
                    public void onNeutralButton(DialogInterface dialog) {

                    }
                });
            }

        }

    }
}
