
package com.visa.vts.certificationapp.model.request;

import com.google.gson.annotations.Expose;


public class EnrollDeviceParams {

    @Expose
    private ChannelSecurityContext channelSecurityContext;
    @Expose
    private DeviceInfo deviceInfo;

    /**
     * 
     * @return
     *     The channelSecurityContext
     */
    public ChannelSecurityContext getChannelSecurityContext() {
        return channelSecurityContext;
    }

    /**
     * 
     * @param channelSecurityContext
     *     The channelSecurityContext
     */
    public void setChannelSecurityContext(ChannelSecurityContext channelSecurityContext) {
        this.channelSecurityContext = channelSecurityContext;
    }

    /**
     * 
     * @return
     *     The deviceInfo
     */
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * 
     * @param deviceInfo
     *     The deviceInfo
     */
    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

}
