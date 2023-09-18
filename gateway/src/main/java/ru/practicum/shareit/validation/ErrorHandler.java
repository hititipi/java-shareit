package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.validation.exception.UnsupportedStatusException;
import ru.practicum.shareit.validation.exception.ValidationException;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.validation.ValidationErrors.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(UnsupportedStatusException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(UNSUPPORTED_STATUS, e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ValidationException handleValidationException(final ValidationException e) {
        log.error(e.getMessage(), e);
        throw e;
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(Throwable e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
