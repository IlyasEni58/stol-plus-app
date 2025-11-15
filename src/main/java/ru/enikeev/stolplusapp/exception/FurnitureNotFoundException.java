package ru.enikeev.stolplusapp.exception;

public class FurnitureNotFoundException extends RuntimeException{
    public FurnitureNotFoundException(String message) {
        super(message);
    }

    public FurnitureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
