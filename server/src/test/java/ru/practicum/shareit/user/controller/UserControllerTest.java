package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1, "userName", "user@email.ru");
    }

    @Test
    void findAll() throws Exception {
        List<UserDto> users = List.of(userDto);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail()), String.class));
        verify(userService, times(1)).findAll();
    }

    @Test
    void addUser() throws Exception {
        when(userService.addUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", Is.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Is.is(userDto.getEmail()), String.class));

        verify(userService, times(1))
                .addUser(any(UserDto.class));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(UserDto.class), anyInt())).thenReturn(userDto);

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", Is.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Is.is(userDto.getEmail()), String.class));

        verify(userService, times(1)).updateUser(any(UserDto.class), anyInt());
    }

    @Test
    void findById() throws Exception {
        when(userService.findById(anyInt())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));

        verify(userService, times(1)).findById(anyInt());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyInt());
    }
}
