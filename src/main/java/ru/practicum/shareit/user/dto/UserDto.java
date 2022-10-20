package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {

    private int id;

    @NotBlank(groups = {Create.class}, message = "Не указано имя")
    private String name;

    @NotNull(groups = {Create.class}, message = "Email не указан")
    @Email(groups = {Create.class, Update.class})
    private String email;
}
