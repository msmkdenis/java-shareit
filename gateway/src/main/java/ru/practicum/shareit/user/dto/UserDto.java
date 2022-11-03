package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;

    @NotBlank(groups = {Create.class}, message = "Не указано имя")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Email не указан")
    @Email(groups = {Create.class, Update.class})
    private String email;
}
