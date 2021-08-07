package com.ranier.parking.exception;

public class ParkingNotFound extends Exception {

    private static final String DEFAULT_MESSAGE = "Parking is not found!";

    public ParkingNotFound() {
        super(DEFAULT_MESSAGE);
    }

    public ParkingNotFound(String message) {
        super(message);
    }
}
