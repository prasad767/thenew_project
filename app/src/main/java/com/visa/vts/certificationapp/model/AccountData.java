package com.visa.vts.certificationapp.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.ContextHelper;
import com.visa.vts.certificationapp.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Chandrakanta_Sahoo on 11/23/2015.
 */
public class AccountData {

    private ArrayList<AccountDetails> accountDetailsArrayList;
    private static AccountData accountData;
    private VTSCertApp mainApp;


    public static AccountData getInstance(){

        if(accountData==null){
            accountData = new AccountData();
        }

        return accountData;
    }

    private AccountData(){
        mainApp = (VTSCertApp) ContextHelper.getContext().getApplicationContext();
        accountDetailsArrayList = getAccountDetailList();
    }


    public void addAccount(AccountDetails accountDetails){

        accountDetailsArrayList = getAccountDetailList();
        accountDetailsArrayList.add(accountDetails);
        saveAccountDetails();
    }

    public void deleteAccount(int index){
        accountDetailsArrayList = getAccountDetailList();
        accountDetailsArrayList.remove(index);
        saveAccountDetails();
    }


    //saveAccountData
    private void saveAccountDetails(){

        Gson gson = new Gson();
        String accountDetailJson = gson.toJson(accountDetailsArrayList);
        Utils.printLog("Saved account detail: " + accountDetailJson);

        mainApp.getPreference().saveString(Constants.PREFERENCE_ACCOUNTDETAIL, accountDetailJson);
    }

    //getAccountData
    public ArrayList<AccountDetails> getAccountDetailList(){

        String accountdetail_json  = mainApp.getPreference().retrieveString(Constants.PREFERENCE_ACCOUNTDETAIL);

        if(accountdetail_json==null)
        {
            return new ArrayList<AccountDetails>();

        }else
        {
            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<AccountDetails>>(){}.getType();

            return gson.fromJson(accountdetail_json,type);
        }

    }

    //get number of accounts
    public int getNoAccounts(){
        return getAccountDetailList().size();
    }
}
