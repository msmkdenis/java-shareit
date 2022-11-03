package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private int id;
    private String description;
    private LocalDateTime created;
}
