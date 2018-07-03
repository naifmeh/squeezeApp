package com.squeeze.squeezeadmin.beans;

public class AuthDeviceBean {

    public DataAuthBean data;

    public AuthDeviceBean() {
    }

    public AuthDeviceBean(DataAuthBean data) {
        this.data = data;
    }

    public DataAuthBean getData() {
        return data;
    }

    public void setData(DataAuthBean data) {
        this.data = data;
    }
}
