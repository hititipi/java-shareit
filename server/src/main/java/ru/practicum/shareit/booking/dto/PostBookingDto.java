package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostBookingDto {

    private int itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}
