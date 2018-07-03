package com.squeeze.squeezeadmin.beans;

public class DataAuthBean {

    public DeviceBean device;
    public String token;

    public DataAuthBean() {
    }

    public DataAuthBean(DeviceBean device, String token) {
        this.device = device;
        this.token = token;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
