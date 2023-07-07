package ru.practicum.shareit.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.validation.exception.UnsupportedStatusException;

import static ru.practicum.shareit.validation.ValidationErrors.UNSUPPORTED_STATUS;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(UnsupportedStatusException e) {
        return new ErrorResponse(UNSUPPORTED_STATUS, "e.getMessage()");
    }
}
