package com.visa.vts.certificationapp.model.request;

import com.visa.cbp.external.common.RiskData;
import com.visa.vts.certificationapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandrakanta_sahoo on 12/11/2015.
 */
public class RiskParams {
    private String cvv2;
    private String billingZip;
    private String addressLine1;
    private String fullName;

    public RiskParams(String cvv2, String billingZip, String addressLine1, String fullName) {
        this.cvv2 = cvv2;
        this.billingZip = billingZip;
        this.addressLine1 = addressLine1;
        this.fullName = fullName;
    }

    public List<RiskData> initializeRiskParams() {
        List<RiskData> mapList = new ArrayList<RiskData>();
        mapList.add(getRiskData(Constants.CVV, cvv2));
        mapList.add(getRiskData(Constants.BILLING_ZIP, billingZip));
        mapList.add(getRiskData(Constants.ACCOUNT_HOLDER_NAME, fullName));

        return mapList;
    }

    private RiskData getRiskData(String name, String value) {
        RiskData riskData = new RiskData();
        riskData.setName(name);
        riskData.setValue(value);
        return riskData;
    }

}
