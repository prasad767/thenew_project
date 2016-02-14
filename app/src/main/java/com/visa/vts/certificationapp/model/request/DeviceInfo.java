
package com.visa.vts.certificationapp.model.request;

import com.google.gson.annotations.Expose;
import com.visa.vts.certificationapp.utils.Utils;


public class DeviceInfo {

    @Expose
    private String deviceBrand;
    @Expose
    private String deviceIDType;
    @Expose
    private String deviceModel;
    @Expose
    private String deviceName;
    @Expose
    private String deviceType;
    @Expose
    private String hostDeviceID;
    @Expose
    private String osBuildID;
    @Expose
    private String osType;
    @Expose
    private String osVersion;
    @Expose
    private String phoneNumber;
    @Expose
    private String deviceManufacturer;



    public void init(String hostDeviceID){

        this.osType = "ANDROID";
        this.osVersion = "4.4.4";
        this.osBuildID = "KTU84M";
        this.deviceType = "WATCH";
        this.deviceIDType = "TEE";
        this.deviceManufacturer = "Samsung";
        this.deviceBrand = "AndroidBrand";
        this.deviceModel = "ANDROID-999";
        this.deviceName = "MyWatch";//getName();
        this.hostDeviceID = hostDeviceID;
        this.phoneNumber = "+14087861234";

        Utils.printLog("init deviceinfo");
    }

    /**
     * 
     * @return
     *     The deviceBrand
     */
    public String getDeviceBrand() {
        return deviceBrand;
    }

    /**
     * 
     * @param deviceBrand
     *     The deviceBrand
     */
    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    /**
     * 
     * @return
     *     The deviceIDType
     */
    public String getDeviceIDType() {
        return deviceIDType;
    }

    /**
     * 
     * @param deviceIDType
     *     The deviceIDType
     */
    public void setDeviceIDType(String deviceIDType) {
        this.deviceIDType = deviceIDType;
    }

    /**
     * 
     * @return
     *     The deviceModel
     */
    public String getDeviceModel() {
        return deviceModel;
    }

    /**
     * 
     * @param deviceModel
     *     The deviceModel
     */
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    /**
     * 
     * @return
     *     The deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * 
     * @param deviceName
     *     The deviceName
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * 
     * @return
     *     The deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * 
     * @param deviceType
     *     The deviceType
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 
     * @return
     *     The hostDeviceID
     */
    public String getHostDeviceID() {
        return hostDeviceID;
    }

    /**
     * 
     * @param hostDeviceID
     *     The hostDeviceID
     */
    public void setHostDeviceID(String hostDeviceID) {
        this.hostDeviceID = hostDeviceID;
    }

    /**
     * 
     * @return
     *     The osBuildID
     */
    public String getOsBuildID() {
        return osBuildID;
    }

    /**
     * 
     * @param osBuildID
     *     The osBuildID
     */
    public void setOsBuildID(String osBuildID) {
        this.osBuildID = osBuildID;
    }

    /**
     * 
     * @return
     *     The osType
     */
    public String getOsType() {
        return osType;
    }

    /**
     * 
     * @param osType
     *     The osType
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * 
     * @return
     *     The osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * 
     * @param osVersion
     *     The osVersion
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * 
     * @return
     *     The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 
     * @param phoneNumber
     *     The phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }
}
