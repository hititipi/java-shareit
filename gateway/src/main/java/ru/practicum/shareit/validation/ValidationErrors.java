package ru.practicum.shareit.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationErrors {

    public static final String RESOURCE_NOT_FOUND = "Ресурс не найден";
    public static final String INVALID_USER_ID = "Некорректно задан пользователь";
    public static final String INVALID_TIME = "Некорректно задано время";
    public static final String ITEM_UNAVAILABLE = "Вещь недоступна";
    public static final String BOOKING_ALREADY_APPROVED = "Бронирование уже подтверждено (отклонено)";
    public static final String UNSUPPORTED_STATUS = "Unknown state: UNSUPPORTED_STATUS";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String BAD_REQUEST = "Bad Request";

}
