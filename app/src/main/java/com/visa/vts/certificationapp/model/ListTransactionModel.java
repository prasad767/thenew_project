package com.visa.vts.certificationapp.model;

/**
 * Created by Manjit.Kaur on 12/15/2015.
 */
public class ListTransactionModel {private  String TransactionName ="";
    private  String TransactionPlace ="";
    private  String TransactionTime ="";
    private  String TransactionBill ="";


    /*********** Set Methods ******************/

    public void setTransactionName(String TransactionName)
    {
        this.TransactionName = TransactionName;
    }

    public void setTransactionPlace(String TransactionPlace)
    {
        this.TransactionPlace = TransactionPlace;
    }

    public void setTransactionTime(String TransactionTime)
    {
        this.TransactionTime = TransactionTime;
    }

    public void setTransactionBill(String TransactionBill){
        this.TransactionBill = TransactionBill;
    }

    /*********** Get Methods ****************/

    public String getTransactionName()
    {
        return this.TransactionName;
    }

    public String getTransactionPlace()
    {
        return this.TransactionPlace;
    }

    public String getTransactionTime()
    {
        return this.TransactionTime;
    }

    public String getTransactionBill()
    {
        return this.TransactionBill;
    }

}
