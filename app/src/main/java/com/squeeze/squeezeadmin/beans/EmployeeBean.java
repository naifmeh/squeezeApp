package com.squeeze.squeezeadmin.beans;

public class EmployeeBean {

    private String id;
    public String firstName;
    public String lastName;
    public String email;
    public boolean authorized;
    public int frequency;
    public long authStarting;
    public long authEnding;

    public EmployeeBean() {
    }

    public EmployeeBean(String id, String firstName, String lastName, String email, boolean authorized, int frequency, long authStarting, long authEnding) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorized = authorized;
        this.frequency = frequency;
        this.authStarting = authStarting;
        this.authEnding = authEnding;
    }

    public EmployeeBean(String firstName, String lastName, String email, boolean authorized,
                        int frequency, long authStarting, long authEnding) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.authorized = authorized;
        this.frequency = frequency;
        this.authStarting = authStarting;
        this.authEnding = authEnding;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getAuthStarting() {
        return authStarting;
    }

    public void setAuthStarting(long authStarting) {
        this.authStarting = authStarting;
    }

    public long getAuthEnding() {
        return authEnding;
    }

    public void setAuthEnding(long authEnding) {
        this.authEnding = authEnding;
    }
}
