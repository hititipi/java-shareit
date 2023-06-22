package ru.practicum.shareit.utils;

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
}
