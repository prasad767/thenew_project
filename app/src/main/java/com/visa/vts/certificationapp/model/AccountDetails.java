package com.visa.vts.certificationapp.model;

import com.visa.cbp.sdk.facade.data.TokenKey;

import java.util.UUID;

/**
 * Created by Chandrakanta_Sahoo on 11/23/2015.
 */
public class AccountDetails {
    private UUID id;
    private String name, balance,last4Digit,expiry;
    private String accountNumber;
    //    private PanInformation panInfo;
    private int smallResource, largeResource, pagerPosition;
    private boolean isSetupForPayInStore, isDefaultAlwaysOnCard;
    private boolean isDefaultCard;
    private AccountType accountType;
        private TokenKey tokenKey;
    //    private Status status;
    private String vProvisionId;
    private String vProvisionedTokenID;


    public String getProvisionAPI() {
        return mProvisionAPI;
    }

    public void setProvisionAPI(String mProvisionAPI) {
        this.mProvisionAPI = mProvisionAPI;
    }



    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }



    public String getMguid() {
        return mguid;
    }

    public void setMguid(String mguid) {
        this.mguid = mguid;
    }

    public String getvPanEnrollmentID() {
        return vPanEnrollmentID;
    }

    public void setvPanEnrollmentID(String vPanEnrollmentID) {
        this.vPanEnrollmentID = vPanEnrollmentID;
    }

    private String mProvisionAPI;
    private String mguid;
    private int sc;
    private String vPanEnrollmentID;







    public AccountDetails(String name,
                          String balance,
                          AccountType accountType,
                          int smallResource,
                          int largeResource,
                          int pagerPosition,
                          String accountNumber,
                          boolean isSetupForPayInStore,
                          boolean isDefaultAlwaysOnCard
    ) {

        id = UUID.randomUUID();
        setBalance(balance);
        setName(name);
        setSmallResource(smallResource);
        setLargeResource(largeResource);
        setAccountType(accountType);
        setPagerPosition(pagerPosition);
        setAccountNumber(accountNumber);
        setSetupForPayInStore(isSetupForPayInStore);

        //        setStatus(Status.ACTIVE);
        // setPanInfo(new PanInformation(id.toString(),accountNumber.substring(accountNumber.length() - 4),PanStatus.NEW.toString()));
    }

//Todo with token key
  /*  public AccountDetails(String l4dcardnumber,
                          String expiry,
                          AccountType accountType,
                          String vProvisionedTokenID,
                          long tokenKey,
                          boolean isDefaultCard
    ) {

       /* setLast4digit(l4dcardnumber);
        setExpiry(expiry);
        setAccountType(accountType);
        setvProvisionedTokenID(vProvisionedTokenID);
        setTokenKey(tokenKey);
        setDefaultCard(isDefaultCard);*/

    public AccountDetails(String l4dcardnumber,
                          String expiry,
                          AccountType accountType,
                          String vProvisionedTokenID,
                          boolean isDefaultCard
    ) {

        setLast4digit(l4dcardnumber);
        setExpiry(expiry);
        setAccountType(accountType);
        setvProvisionedTokenID(vProvisionedTokenID);
        setDefaultCard(isDefaultCard);


    }
    public AccountDetails() {

    }
    public String getLast4digit(){return last4Digit;}
    public void setLast4digit(String cardnumber ){this.last4Digit = cardnumber;}



    public String getExpiry(){return expiry;}
    public void setExpiry(String expirydate ){this.expiry = expirydate ;}

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public int getSmallResource() {
        return smallResource;
    }

    public void setSmallResource(int smallResource) {
        this.smallResource = smallResource;
    }

    public int getLargeResource() {
        return largeResource;
    }

    public void setLargeResource(int largeResource) {
        this.largeResource = largeResource;
    }

    public int getPagerPosition() {
        return pagerPosition;
    }

    public void setPagerPosition(int pagerPosition) {
        this.pagerPosition = pagerPosition;
    }

    public boolean isSetupForPayInStore() {
        return isSetupForPayInStore;
    }

    public void setSetupForPayInStore(boolean isSetupForNfc) {
        this.isSetupForPayInStore = isSetupForNfc;
    }

    //vprovisionTokenId save
    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

   /* //vprovisionId save
    public String getvProvisionId() {
        return vProvisionId;
    }

    public void setvProvisionId(String vProvisionId) {
        this.vProvisionId = vProvisionId;
    }*/



//    public PanInformation getPanInfo() {
//        return panInfo;
//    }
//
//    public void setPanInfo(PanInformation panInfo) {
//        this.panInfo = panInfo;
//    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }




       public TokenKey getTokenKey() {
            return tokenKey;
        }
        public void setTokenKey(TokenKey tokenKey) {
            this.tokenKey = tokenKey;
        }

    public enum AccountType {
        SAVINGS, CHECKING, CREDIT_CARD, NONE;

        public static AccountType toAccountType(String accountType) {
            try {
                return valueOf(accountType);
            } catch (Exception ex) {
                // For error cases
                return NONE;
            }
        }
    }

    /**
     * This makes sure that for this particular device,
     * does the CBP-SDK have a token existing for the card in consideration.
     * @param last4 of the card number
     * @return true if token exists, false otherwise
     **/
    private boolean checkIfCardTokenExists(String last4) {
        return true;
    }

//    public Status getStatus() {
//        return status;
////    }
//
//    public void setStatus(Status status) {
//        this.status = status;
//    }


    public void setDefaultCard(boolean isDefaultCard){this.isDefaultCard = isDefaultCard;}

    public boolean isCardDefault(){return isDefaultCard;}

}
