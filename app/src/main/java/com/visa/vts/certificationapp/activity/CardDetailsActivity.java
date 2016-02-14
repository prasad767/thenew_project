package com.visa.vts.certificationapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.adapter.TransactionListAdapter;
import com.visa.vts.certificationapp.model.AccountData;
import com.visa.vts.certificationapp.model.AccountDetails;
import com.visa.vts.certificationapp.model.ListTransactionModel;
import com.visa.vts.certificationapp.network.BackendEndPoints;
import com.visa.vts.certificationapp.network.RestController;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.Utils;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CardDetailsActivity extends BaseActivity {
    private ListView listTransaction;
    private TransactionListAdapter adapter;
    private Activity mContext;
    private int mCardIndex;
    private TextView mCardDetailsTextView;
    private ArrayList<ListTransactionModel> transactionDetailsList = new ArrayList<ListTransactionModel>();
    private com.visa.vts.certificationapp.utils.ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);


        processToolbar();

        getBundleValue();

        classAndWidgetInitialize();

        getCardInfo();

        getCardMetaData();

        AccountDetails account = AccountData.getInstance().getAccountDetailList().get(mCardIndex);
        String guid = account.getMguid();
        mCardDetailsTextView.setText("Card Number : ..." + account.getLast4digit() +
                "\n\nCard Expiry : " + account.getExpiry() +
                "\n\nCard MetaData  guid::::" + getGuid() + "  ::vpanenrollid:::" + getvPanEnrollmentID());
        setListData();
        Resources res = getResources();
        adapter = new TransactionListAdapter(this, transactionDetailsList, res);
        listTransaction.setAdapter(adapter);
    }

    private void getCardInfo() {

    }


    /**
     * Initialize class And Widget
     */
    private void classAndWidgetInitialize() {

        mContext = CardDetailsActivity.this;
        mProgressDialog = new com.visa.vts.certificationapp.utils.ProgressDialog(mContext);

        listTransaction = (ListView) findViewById(R.id.listview_transaction);
        mCardDetailsTextView = (TextView) findViewById(R.id.card_details_info);


    }

    /**
     * get Bundle Value
     */
    private void getBundleValue() {
        mCardIndex = getIntent().getExtras().getInt("mCardIndex");
        Utils.printLog("CardDetailsActivity = " + mCardIndex);

    }

    private void getCardMetaData() {

        mProgressDialog.show();
        mProgressDialog.setOnDismissListener(new com.visa.vts.certificationapp.utils.ProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        BackendEndPoints service = RestController.getApi();
        //Call<String> call = service.getContent(getGuid());
        // Call<String> call = service.getContent(getGuid(),getvPanEnrollmentID());
        Call<String> call = service.getContent(getGuid(), Constants.SANDBOX_API_KEY);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(final Response<String> response, Retrofit retrofit) {
                Utils.printLog("Response Code " + response.code());
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Throwable cause) {
                cause.getLocalizedMessage();
                mProgressDialog.dismiss();

                Log.v("TAG", "failure");

            }
        });


    }

    private String getGuid() {
        int cardIndex = getIntent().getExtras().getInt("mCardIndex");
        AccountDetails account = AccountData.getInstance().getAccountDetailList().get(cardIndex);
        String aguid = account.getMguid();
        Log.d("save", "" + aguid);
        return aguid;
    }

    private String getvPanEnrollmentID() {
        int cardIndex = getIntent().getExtras().getInt("mCardIndex");
        AccountDetails account = AccountData.getInstance().getAccountDetailList().get(cardIndex);
        String vPanEnrollmentID = account.getvPanEnrollmentID();
        Log.d("save vPanEnrollmentID", "" + vPanEnrollmentID);
        return vPanEnrollmentID;
    }


    private void setListData() {
        final ListTransactionModel transaction1 = new ListTransactionModel();
        transaction1.setTransactionName("Panera Bread king Street");
        transaction1.setTransactionPlace("In store");
        transaction1.setTransactionTime("Today");
        transaction1.setTransactionBill("$13");


        final ListTransactionModel transaction2 = new ListTransactionModel();
        transaction2.setTransactionName("Bluebottle Ferry building");
        transaction2.setTransactionPlace("E-commerce");
        transaction2.setTransactionTime("Today");
        transaction2.setTransactionBill("$29");

        final ListTransactionModel transaction3 = new ListTransactionModel();
        transaction3.setTransactionName("Seamless");
        transaction3.setTransactionPlace("In store");
        transaction3.setTransactionTime("Yesterday");
        transaction3.setTransactionBill("$19");


        final ListTransactionModel transaction4 = new ListTransactionModel();
        transaction4.setTransactionName("Target");
        transaction4.setTransactionPlace("E-commerce");
        transaction4.setTransactionTime("Yesterday");
        transaction4.setTransactionBill("$9");

        final ListTransactionModel transaction5 = new ListTransactionModel();
        transaction5.setTransactionName("Lee's Delt");
        transaction5.setTransactionPlace("E-commerce");
        transaction5.setTransactionTime("Yesterday");
        transaction5.setTransactionBill("$29");

        transactionDetailsList.add(transaction1);
        transactionDetailsList.add(transaction2);
        transactionDetailsList.add(transaction3);
        transactionDetailsList.add(transaction4);
        transactionDetailsList.add(transaction5);
    }


    /*****************
     * This function used by adapter
     ****************/
    private void onItemClick(int mPosition) {
        ListTransactionModel tempValues = (ListTransactionModel) transactionDetailsList.get(mPosition);
    }

}
