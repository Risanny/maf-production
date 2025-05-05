package com.maf.production.model;

public enum AvailabilityStatus {
    AVAILABLE("Включено"),
    RESERVED("В резерве"),
    NOT_PRODUCED("Не производится");

    private final String displayName;

    AvailabilityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
