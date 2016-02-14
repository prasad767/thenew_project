
package com.visa.vts.certificationapp.model.request;


public class DeviceCert {

    private String certUsage;
    private String certFormat;
    private String certValue;

    public void init(String certUsage, String certValue) {
        //Call this function if and only if you want to set the hard coded values
        this.certUsage = certUsage;
        this.certFormat = "X509";
        this.certValue = certValue;
    }

    /**
     * 
     * @return
     *     The certFormat
     */
    public String getCertFormat() {
        return certFormat;
    }

    /**
     * 
     * @param certFormat
     *     The certFormat
     */
    public void setCertFormat(String certFormat) {
        this.certFormat = certFormat;
    }

    /**
     * 
     * @return
     *     The certUsage
     */
    public String getCertUsage() {
        return certUsage;
    }

    /**
     * 
     * @param certUsage
     *     The certUsage
     */
    public void setCertUsage(String certUsage) {
        this.certUsage = certUsage;
    }

    /**
     * 
     * @return
     *     The certValue
     */
    public String getCertValue() {
        return certValue;
    }

    /**
     * 
     * @param certValue
     *     The certValue
     */
    public void setCertValue(String certValue) {
        this.certValue = certValue;
    }

}
