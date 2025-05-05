package com.maf.production.model;

public enum ProductionType {
    USN_RK("УСН РК"),
    INDIVIDUAL("Индивидуальное");

    private final String displayName;

    ProductionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}