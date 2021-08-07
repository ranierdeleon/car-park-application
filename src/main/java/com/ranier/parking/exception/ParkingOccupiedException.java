package com.ranier.parking.exception;

public class ParkingOccupiedException extends Exception {

    private static final String DEFAULT_MESSAGE = "Parking is occupied!";

    public ParkingOccupiedException() {
        super(DEFAULT_MESSAGE);
    }

    public ParkingOccupiedException(String message) {
        super(message);
    }
}
