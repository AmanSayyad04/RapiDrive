package com.findpath.smartvehicles.activity;

public class Slot {
    private String slotId;
    private String slotNumber;
    private String pricePerUnit;
    private String selectedOption;
    private boolean isOccupied = false;
    private boolean isSelected;

    public Slot() {
        // Default constructor required for Firestore
    }

    public Slot(String slotNumber, String pricePerUnit, String selectedOption, boolean isOccupied) {
        this.slotNumber = slotNumber;
        this.pricePerUnit = pricePerUnit;
        this.selectedOption = selectedOption;
        this.isOccupied = isOccupied;
    }

    // Generate getters and setters

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(String pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
