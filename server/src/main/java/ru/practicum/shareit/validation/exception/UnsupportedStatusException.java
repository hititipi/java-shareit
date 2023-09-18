package ru.practicum.shareit.validation.exception;

import org.springframework.http.HttpStatus;

public class UnsupportedStatusException extends ValidationException {

    public UnsupportedStatusException(HttpStatus status, String message) {
        super(status, message);
    }
}
