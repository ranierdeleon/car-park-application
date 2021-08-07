package com.ranier.parking.exception;

public class VehicleRejectException extends Exception {

    private static final String DEFAULT_MESSAGE = "Vehicle rejected!";

    public VehicleRejectException() {
        super(DEFAULT_MESSAGE);
    }

    public VehicleRejectException(String message) {
        super(message);
    }
}
