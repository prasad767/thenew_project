
package com.visa.vts.certificationapp.model;

import com.google.gson.annotations.Expose;

public class PaymentInstrument {

    @Expose
    private BillingAddress billingAddress;
    @Expose
    private String accountNumber;
    @Expose
    private ExpirationDate expirationDate;
    @Expose
    private String name;
    @Expose
    private String cvv2;

    /**
     * 
     * @return
     *     The billingAddress
     */
    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    /**
     * 
     * @param billingAddress
     *     The billingAddress
     */
    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * 
     * @return
     *     The accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * 
     * @param accountNumber
     *     The accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * 
     * @return
     *     The expirationDate
     */
    public ExpirationDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * 
     * @param expirationDate
     *     The expirationDate
     */
    public void setExpirationDate(ExpirationDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The cvv2
     */
    public String getCvv2() {
        return cvv2;
    }

    /**
     * 
     * @param cvv2
     *     The cvv2
     */
    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

}
