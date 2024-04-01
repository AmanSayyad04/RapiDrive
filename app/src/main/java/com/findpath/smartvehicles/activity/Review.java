package com.findpath.smartvehicles.activity;

import com.google.firebase.firestore.PropertyName;

public class Review {

    @PropertyName("text")
    private String text;

    @PropertyName("rating")
    private float rating;

    @PropertyName("garageName")
    private String garageName;

    @PropertyName("userId")
    private String userId;

    // Required public no-argument constructor
    public Review() {
        // Default constructor required for Firestore
    }

    public Review(String text, float rating, String garageName, String userId) {
        this.text = text;
        this.rating = rating;
        this.garageName = garageName;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
