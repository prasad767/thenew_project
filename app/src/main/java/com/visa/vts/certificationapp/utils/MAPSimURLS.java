package com.visa.vts.certificationapp.utils;

import com.visa.vts.certificationapp.VTSCertApp;

public class MAPSimURLS {

private  static MAPSimURLS mapSimURLS;
    VTSCertApp VTSCertApp;
String environmentString;
public String DOMAIN;
    public String ROOT;
    public  String ENROLL_DEVICE;
    public  String ENROLL_PAN ;
    public  String PROVISION_PAN;
    public  String CONFIRM_PROVISION;

    public  String REPLENISH;
    public  String CONFIRM_REPLENISH;

    public  String LCM_DELETE;
    public  String LCM_SUSPEND;
    public  String LCM_RESUME;




    public static MAPSimURLS getInstance(){
        if(mapSimURLS==null){
            mapSimURLS = new MAPSimURLS();
        }
        return mapSimURLS;
    }

    private MAPSimURLS(){

        VTSCertApp = (VTSCertApp) ContextHelper.getContext().getApplicationContext();
        setEnvironmentString(VTSCertApp.getPreference().retrieveString(Constants.Preference_Environment).toLowerCase());

       if(getEnvironmentString().equalsIgnoreCase("sandbox"))
       {
        setDOMAIN("https://sandbox.digital.visa.com");
       }else if(getEnvironmentString().equalsIgnoreCase("occ_sandbox"))
       {
            setDOMAIN("https://cert1.secure.checkout.visa.com");
            setEnvironmentString("occsandbox"); //Temp fix for action name
        }
    }


//    public static String CERT1_ENROLL = "http://sl73commapd022.visa.com:8444/VTS/dmpd/enrolldevice?envName=cert1&action=enrolldevice";


    public String getDOMAIN() {
        return DOMAIN;
    }

    public void setDOMAIN(String DOMAIN) {
        this.DOMAIN = DOMAIN;
    }

    public String getROOT() {
        ROOT= getDOMAIN()+"/VTS/";
        return ROOT;
    }

    public void setROOT(String ROOT) {
        this.ROOT = ROOT;
    }

    public String getEnvironmentString() {
        return environmentString;
    }

    public void setEnvironmentString(String environmentString) {
        this.environmentString = environmentString;
    }

    public String getENROLL_DEVICE() {

        ENROLL_DEVICE = getROOT()+"enrolldevice?envName="+getEnvironmentString()+"&action=enrolldevice";
        return ENROLL_DEVICE;
    }
    public void setENROLL_DEVICE(String ENROLL_DEVICE) {
        this.ENROLL_DEVICE = ENROLL_DEVICE;
    }


    public String getENROLL_PAN() {
        ENROLL_PAN = getROOT()+"panEnrollments?apiKey="+Constants.SANDBOX_API_KEY;
        return ENROLL_PAN;
    }

    public void setENROLL_PAN(String ENROLL_PAN) {
        this.ENROLL_PAN = ENROLL_PAN;
    }

    public String getPROVISION_PAN() {

        PROVISION_PAN = getROOT()+"provisionpan?envName="+getEnvironmentString()+"&action=provisiontoken";
        return PROVISION_PAN;
    }

    public void setPROVISION_PAN(String PROVISION_PAN) {
        this.PROVISION_PAN = PROVISION_PAN;
    }

    public String getCONFIRM_PROVISION() {

        CONFIRM_PROVISION = getROOT()+"confirmprovision?envName="+getEnvironmentString()+"&action=confirmprovision";
        return CONFIRM_PROVISION;
    }

    public void setCONFIRM_PROVISION(String CONFIRM_PROVISION) {
        this.CONFIRM_PROVISION = CONFIRM_PROVISION;
    }

    public String getREPLENISH() {
        REPLENISH =  getROOT()+"replenish?envName="+getEnvironmentString()+"&action=replenish";
        return REPLENISH;
    }

    public void setREPLENISH(String REPLENISH) {
        this.REPLENISH = REPLENISH;
    }

    public String getCONFIRM_REPLENISH() {
         CONFIRM_REPLENISH =getROOT()+"confirmreplenish?envName="+getEnvironmentString()+"&action=confirmreplenish";
        return CONFIRM_REPLENISH;
    }

    public void setCONFIRM_REPLENISH(String CONFIRM_REPLENISH) {
        this.CONFIRM_REPLENISH = CONFIRM_REPLENISH;
    }

    public String getLCM_DELETE() {
        return LCM_DELETE= getROOT()+"tokenLCM?envName="+getEnvironmentString()+"&action=delete";
    }

    public void setLCM_DELETE(String LCM_DELETE) {
        this.LCM_DELETE = LCM_DELETE;
    }

    public String getLCM_SUSPEND() {
        return LCM_SUSPEND =   getROOT()+"tokenLCM?envName="+getEnvironmentString()+"&action=suspend";
    }

    public void setLCM_SUSPEND(String LCM_SUSPEND) {
        this.LCM_SUSPEND = LCM_SUSPEND;
    }

    public String getLCM_RESUME() {
        return LCM_RESUME = getROOT()+"tokenLCM?envName="+getEnvironmentString()+"&action=resume";
    }

    public void setLCM_RESUME(String LCM_RESUME) {
        this.LCM_RESUME = LCM_RESUME;
    }
}
