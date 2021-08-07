package com.ranier.parking.exception;

public class InvalidInputException extends Exception {

    private static final String DEFAULT_MESSAGE = "Invalid input!";

    public InvalidInputException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidInputException(String message) {
        super(message);
    }
}
