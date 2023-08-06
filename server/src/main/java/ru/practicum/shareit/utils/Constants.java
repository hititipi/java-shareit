package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public final String USER_ID_HEADER = "X-Sharer-User-Id";
    public final int DEFAULT_FROM_INT = 0;
    public final String DEFAULT_FROM_VALUE = String.valueOf(DEFAULT_FROM_INT);
    public final int DEFAULT_SIZE_INT = 20;
    public final String DEFAULT_SIZE_VALUE = String.valueOf(DEFAULT_SIZE_INT);
}
