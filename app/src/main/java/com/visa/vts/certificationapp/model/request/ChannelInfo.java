
package com.visa.vts.certificationapp.model.request;

import com.google.gson.annotations.Expose;


public class ChannelInfo {

    @Expose
    private String encryptionScheme;

    /**
     * 
     * @return
     *     The encryptionScheme
     */
    public String getEncryptionScheme() {
        return encryptionScheme;
    }

    /**
     * 
     * @param encryptionScheme
     *     The encryptionScheme
     */
    public void setEncryptionScheme(String encryptionScheme) {
        this.encryptionScheme = encryptionScheme;
    }

}
