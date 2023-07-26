package ru.practicum.shareit.validation;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.validation.exception.UnsupportedStatusException;
import ru.practicum.shareit.validation.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.validation.ValidationErrors.RESOURCE_NOT_FOUND;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();


    @Test
    public void handleUnsupportedStatusExceptionTest() {
        UnsupportedStatusException e = new UnsupportedStatusException(HttpStatus.BAD_REQUEST, "Unknown state: UNSUPPORTED_STATUS");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    public void handleValidationsExceptionTest() {
        ValidationException e = new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND);
        ValidationException gottenException = assertThrows(ValidationException.class, () -> handler.handleValidationException(e));
        assertNotNull(gottenException);
        assertEquals(gottenException.getMessage(), RESOURCE_NOT_FOUND);
    }

    @Test
    public void handleBadRequestStatusExceptionTest() {
        ValidationException e = new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
        ErrorResponse errorResponse = handler.handleInternalServerError(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "INTERNAL_SERVER_ERROR");
    }

   /* @Test
    public void handleOwnerNotFoundExceptionTest() {
        OwnerNotFoundException e = new OwnerNotFoundException("Не найден владелец вещи ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), e.getMessage());
    }*/

    /*@Test
    public void handleDeniedAccessExceptionTest() {
        DeniedAccessException e = new DeniedAccessException("Отказано в доступе ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), e.getMessage());
    }*/

   /* @Test
    public void handleNoSuchElementExceptionTest() {
        NoSuchElementException e = new NoSuchElementException("Ошибка поиска элемента 404: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Ошибка поиска элемента 404: ");
        assertEquals(errorResponse.getDescription(), "Ошибка поиска элемента 404: ");
    }*/

    /*@Test
    public void handleUnavailableBookingExceptionTest() {
        UnavailableBookingException e = new UnavailableBookingException("Ошибка бронирования 400: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Ошибка бронирования 400: ");
    }*/

   /* @Test
    public void handleIllegalArgumentExceptionTest() {
        IllegalArgumentException e = new IllegalArgumentException("Передано недопустимое значение 400: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Передано недопустимое значение 400: ");
    }*/



   /* @Test
    public void handleInvalidBookingExceptionTest() {
        InvalidBookingException e = new InvalidBookingException("недопустимое бронирование 404: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "недопустимое бронирование 404: ");
    }*/

   /* @Test
    public void handleCommentExceptionTest() {
        CommentException e = new CommentException("невозможно оставить комментарий 400: ");
        ErrorResponse errorResponse = handler.handle(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "невозможно оставить комментарий 400: ");
    }*/
}
