
package com.visa.vts.certificationapp.model.request;

import com.google.gson.annotations.Expose;


public class VtsCert {

    @Expose
    private String certUsage;
    @Expose
    private String vCertificateID;

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
     *     The vCertificateID
     */
    public String getVCertificateID() {
        return vCertificateID;
    }

    /**
     * 
     * @param vCertificateID
     *     The vCertificateID
     */
    public void setVCertificateID(String vCertificateID) {
        this.vCertificateID = vCertificateID;
    }

}
