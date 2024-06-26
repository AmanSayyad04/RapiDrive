package com.findpath.smartvehicles.adapter;
public class Owner {
    private String userId;
    private String email;

    public Owner() {
        // Default constructor required for calls to DataSnapshot.getValue(Owner.class)
    }

    public Owner(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}