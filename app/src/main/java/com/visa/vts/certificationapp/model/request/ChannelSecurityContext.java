
package com.visa.vts.certificationapp.model.request;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;


public class ChannelSecurityContext {

    @Expose
    private ChannelInfo channelInfo;
    @Expose
    private List<DeviceCert> deviceCerts = new ArrayList<DeviceCert>();
    @Expose
    private List<VtsCert> vtsCerts = new ArrayList<VtsCert>();

    /**
     * 
     * @return
     *     The channelInfo
     */
    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    /**
     * 
     * @param channelInfo
     *     The channelInfo
     */
    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    /**
     * 
     * @return
     *     The deviceCerts
     */
    public List<DeviceCert> getDeviceCerts() {
        return deviceCerts;
    }

    /**
     * 
     * @param deviceCerts
     *     The deviceCerts
     */
    public void setDeviceCerts(List<DeviceCert> deviceCerts) {
        this.deviceCerts = deviceCerts;
    }

    /**
     * 
     * @return
     *     The vtsCerts
     */
    public List<VtsCert> getVtsCerts() {
        return vtsCerts;
    }

    /**
     * 
     * @param vtsCerts
     *     The vtsCerts
     */
    public void setVtsCerts(List<VtsCert> vtsCerts) {
        this.vtsCerts = vtsCerts;
    }

}
