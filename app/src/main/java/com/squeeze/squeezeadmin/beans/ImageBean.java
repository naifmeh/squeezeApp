package com.squeeze.squeezeadmin.beans;

public class ImageBean {

    public long timestamp;
    public String name;
    public String filename;
    public String data;

    public ImageBean() {
    }

    public ImageBean(long timestamp, String name, String filename, String data) {
        this.timestamp = timestamp;
        this.name = name;
        this.filename = filename;
        this.data = data;
    }

    public ImageBean(long timestamp, String filename, String data) {
        this.timestamp = timestamp;
        this.filename = filename;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
