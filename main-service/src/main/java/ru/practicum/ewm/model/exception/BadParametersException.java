package ru.practicum.ewm.model.exception;

public class BadParametersException extends RuntimeException {
    public BadParametersException(final String message) {
        super(message);
    }
}
