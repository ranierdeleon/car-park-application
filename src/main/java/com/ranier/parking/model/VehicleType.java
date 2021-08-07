package com.ranier.parking.model;

import java.math.BigDecimal;

public enum VehicleType {
    CAR("Car", BigDecimal.valueOf(2)), MOTORCYCLE("Motorcycle", BigDecimal.valueOf(1));

    public final String label;
    public final BigDecimal rate;

    private VehicleType(String label, BigDecimal rate) {
        this.label = label;
        this.rate = rate;
    }
}
