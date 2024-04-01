package com.findpath.smartvehicles.activity;

public class ChargingStation {
    private String chargingStationName;
    private String latitude;
    private String longitude;
    private String whatsappNumber;
    private String email;

    public ChargingStation() {
        // Default constructor required for Firestore
    }

    public ChargingStation(String chargingStationName, String latitude, String longitude, String whatsappNumber, String email) {
        this.chargingStationName = chargingStationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.whatsappNumber = whatsappNumber;
        this.email = email;
    }

    // Generate getters and setters
    public String getChargingStationName() {
        return chargingStationName;
    }

    public void setChargingStationName(String chargingStationName) {
        this.chargingStationName = chargingStationName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
