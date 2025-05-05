package com.maf.production.model;

import lombok.Getter;

@Getter
public enum AvailabilityStatus {
    AVAILABLE("Включено"),
    RESERVED("В резерве"),
    NOT_PRODUCED("Не производится");

    private final String displayName;

    AvailabilityStatus(String displayName) {
        this.displayName = displayName;
    }

}
