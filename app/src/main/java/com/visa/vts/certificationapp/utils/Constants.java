package com.visa.vts.certificationapp.utils;

/**
 * Created by prrathin on 11/18/15.
 */
public interface Constants {


    int FRAGMENT_EMPTY_WALLET = 0;
    int FRAGMENT_CARD_LIST = 1;
    int FRAGMENT_ADD_CARD = 2;

    //Preference Keys
     String Preference_Environment = "Pref_Env";

    //Device ID
    String Preference_DeviceID = "Device_ID";

    //Account Details
     String PREFERENCE_ACCOUNTDETAIL = "account_details";

    //Enable Log
     String PREFERENCE_ENABLELOG = "enable_log";

    //Response Body
    String PREFERENCE_RESPONSEBODY = "response_body";

     String DEFAULT_CARD_MESSAGE = "DEFAULT_CARD_MESSAGE";
     int CARD_ACTION_REQUEST_CODE  = 1;

    int STATUS_SUCCESS =200;
    int STATUS_SUCCESS1 =201;
    int STATUS_FAILURE =1;

    int REQUEST_ENROLL_DEVICE =1;
    int REQUEST_ENROLL_PAN =2;
    int REQUEST_ADD_REQUEST =3;
    int REQUEST_PROVISIONING =4;
    int REQUEST_CONFIRM_PROVISIONING =5;
    int REQUEST_LCM =6;


    int FLOW_NONE =-1;
    int FLOW_ADD_A_CARD =0;
    int FLOW_REMOVE_CARD =1;
    int FLOW_SUSPEND =2;
    int FLOW_RESUME =3;

    //cardlist command action
    int CMD_SUSPEND = 0;
    int CMD_RESUME =1;
    int CMD_DELETE=2;
    int CMD_REPLENISH=3;
    int CMD_SETAS_DEFAULT=4;
    int CMD_CONFIRM_REPLENISH=5;


    //Header KEYS
     String CORRELLATION_ID = "X-CORRELATION-ID";

    //Enroll-DeviceCerts
     String CERT_CONFIDENTIALITY = "CONFIDENTIALITY";
     String CERT_INTEGRITY = "INTEGRITY";
     String CERT_DEVICE_ROOT = "DEVICE_ROOT";
     String CERT_FORMAT_VALUE = "X509";

     String ENCRYPTION_SCHEME = "RSA_PKI";


    //Preference Keys
     String Preference_ENROLLED = "Pref_Enroll";
     String Preference_ACCESSTOKEN = "Pref_AccessToken";

    //Extras Keys
     String TERMSCONDITIONID = "termsconditionid";
     String PANENROLLMENTID = "panenrollmentid";
     String UNSUPPORTED_PRESENTATIONTYPES = "unsupportedPresentationTypes";
     String PRESENTATIONTYPE_NFC = "NFC-HCE";
     String PRESENTATIONTYPE_MST = "MST";
     String PRESENTATIONTYPE_INAPP = "INAPP";

     String VProvisionedTokenID = "vprovisiontokenid";
    String vPanEnrollmentID = "vPanEnrollmentID";
    String mProvisionAPI="mProvisionAPI";
    String mSC="mSC";
     String STEP_UP = "stepUp";
     String TOKENKEY = "tokenkey";


     int NO_CARD_SELECTED_VALUE = -1;

    //SANDBOX PROFILES
    //1.SDK team profile
     String SANDBOX_API_KEY = "NOLVJ3EZ02A5ZNGOJIHC13y9daLe_QaWrdboJHOv5LonM2QKI";
     String SANDBOX_SHARED_SECRET = "-5KQ@NQMZ0NMxmOxPXoEu29dzSY@{mH0IpWUp2Hi";
     String SANDBOX_CLIENTID = "56f783d3-8621-3b57-791a-12417aa7a101";
     String SANDBOX_EXTERNAL_APPID = "Appid4321";

     String ApiNameEnrollDevice ="VTS/dmpd/enrolldevice?envName=sandbox&action=enrolldevice&deviceid=970681247d72f4cb12553434";

    //String SANDBOX_API_KEY = "QHFO03HHV13Q7MKM1XW613eHlL76wQ9SmG-wGupX3fhD7vBZU";
    //String SANDBOX_SHARED_SECRET = "28Iq@UBTIyJjWaLpKTdeQ1vXB3QH#A0RyC8BknDt";
    //String SANDBOX_CLIENTID = "4e707183-5853-cf80-f6b2-1ed65ccef901";
   //String SANDBOX_EXTERNAL_APPID = "appid1234";

//new credentials
    //public static final String SANDBOX_API_KEY = "W09UWJHIBL5QSWL33JKQ13wPqsy2aASGC3M5jhZdjIwNHLAmc";
    //public static final String SANDBOX_SHARED_SECRET = "OgnrlJencfFZOyMwlw7ELjja9SiV#y{x+G6RQQZA";
    //public static final String SANDBOX_CLIENTID = "3ab57cfb-5922-adce-b5ba-1c2e32f29c01";
    //public static final String SANDBOX_EXTERNAL_APPID = "visacertapp";



    //PROVISION
     String walletAccountEmailId = "DMPDIntegrationSupportTeam@visa.com";
     String walletAccountID = "710";
     String walletAccountHash = "zmIoP0aEmZTgjK25dalVZcFPhv7peSkHsTp9czJAKYg";


     String IPV4ADDRESS = "10.240.2.123";
     String LOCATION = "123.12345678/-09878768761";

    //ENROLLDEVICE
    String vClientID = "123.12345678/-09878768761";
    String clientDeviceID = "mujeeb987654";

    //RISK DATA
     String CVV = "cvv2";
     String BILLING_ZIP = "billingZip";
     String ADDRESS_LINE_1 = "line1";
     String ACCOUNT_HOLDER_NAME = "accountHolderName";
//
      String vProvisionedTokenID = "adc24340ad9511e4bec7e2fd83f838a9";
    String guid = "guid";

    enum ADD_CARD_FLOW{
        ENROLE_DEVICE,
        ENROLE_PAN,
        ADD_CARD,
        PROVISIONING,
        CONFIRM_PROVISIONING
    }

}
