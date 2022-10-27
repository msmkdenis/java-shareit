package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;
    @MockBean
    UserRepository userRepository;

    private final User user = new User(1, "userName", "user@email.ru");

    @Test
    void addUser() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDto = userService.addUser(UserMapper.toUserDto(user));

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void findById() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userDto = userService.findById(user.getId());

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser() {
        User userUpdate = new User(1, "newUserName", "newUser@email.ru");
        UserDto userDtoUp = new UserDto(userUpdate.getId(), userUpdate.getName(), userUpdate.getEmail());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(userUpdate);

        UserDto result = userService.updateUser(userDtoUp, user.getId());

        assertThat(userUpdate.getId(), equalTo(user.getId()));
        assertEquals(userUpdate.getName(), result.getName());
        assertEquals(userUpdate.getEmail(), result.getEmail());
    }

    @Test
    void findAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.findAll();

        assertThat(users, equalTo(List.of(UserMapper.toUserDto(user))));
    }

    @Test
    void deleteUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        userService.deleteUser(UserMapper.toUserDto(user).getId());
        List<User> users = userRepository.findAll();

        assertEquals(0, users.size());
    }

    @Test
    void updateUserWithNullNameAndEmail() {
        User userUpdate = new User(1, user.getName(), user.getEmail());
        UserDto userDtoUp = new UserDto(userUpdate.getId(), null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(userUpdate);

        UserDto result = userService.updateUser(userDtoUp, user.getId());

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void findByWrongId() {
        assertThrows(EntityNotFoundException.class, () -> userService.findById(999));
    }
}
