package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.model.BookingState;

public class Messages {

    public static String getAllUsers() {
        return "Запрос на получение всех пользователей.";
    }

    public static String getUser(int id) {
        return String.format("Запрос на получение пользователя: id = %d", id);
    }

    public static String addUser() {
        return "Запрос на создание пользователя";
    }

    public static String updateUser(int id) {
        return String.format("Запрос на обновление пользователя: id = %d", id);
    }

    public static String deleteUser(int id) {
        return String.format("Запрос на удаление пользователя: id = %d", id);
    }

    public static String addItem() {
        return "Запрос на создание вещи";
    }

    public static String updateItem(int id) {
        return String.format("Запрос на обновление вещи: id = %d", id);
    }

    public static String getAllItems(int userId) {
        return String.format("Запрос на получение всех вещей для пользователя: id = %d", userId);
    }

    public static String getItem(int itemId) {
        return String.format("Запрос на получение вещи: id = %d", itemId);
    }

    public static String findItems(String text) {
        return String.format("Запрос на поиск вещи: text = %s", text);
    }

    public static String addBooking() {
        return "Запрос на создание бронирования";
    }

    public static String approveBooking(int bookingId, int userId, boolean approve) {
        if (approve) {
            return String.format("Запрос на подтверждение бронирования: booking_id = %d, user_id = %d", bookingId, userId);
        } else {
            return String.format("Запрос на отклонение бронирования: booking_id = %d, user_id = %d", bookingId, userId);
        }
    }

    public static String findBooking(int bookingId, int userId) {
        return String.format("Запрос на получение бронирования: booking_id = %d, user_id = %d", bookingId, userId);
    }

    public static String findAllBookings(BookingState state, int userId) {
        return String.format("Запрос на получение всех бронирований: user_id = %d, state = %s", userId, state);
    }

    public static String findAllBookingsForOwner(int ownerId, BookingState state) {
        return String.format("Запрос на получение бронирований для всех вещей: owner_id = %d, state = %s", ownerId, state);
    }

    public static String addComment(int itemId, int userId) {
        return String.format("Запрос на добавление комментария: booking_id = %d, user_id = %d", itemId, userId);
    }

    public static String addItemRequest(int requestorId) {
        return String.format("Запрос на добавление запроса: requestor_id = %d", requestorId);
    }

    public static String getItemRequestsForOwner(int requestorId) {
        return String.format("Запрос на получение запросов пользователя: requestor_id = %d", requestorId);
    }

    public static String getAllRequestForUser(int userId) {
        return String.format("Запрос на получение запросов для пользователя: user_id = %d", userId);
    }

    public static String getRequestById(int requestId, int userId) {
        return String.format("Запрос на получение запроса:  request_id = %d,  user_id = %d", requestId, userId);
    }
}
