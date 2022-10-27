package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserServiceImpl userService;

    @Test
    void addUser() {
        UserDto userDto = UserMapper.toUserDto(new User(1, "userName", "user@email.ru"));
        userService.addUser(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name", User.class);
        User user = query
                .setParameter("name", userDto.getName())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        User user = new User(2, "userName", "user@email.ru");
        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userOut = query
                .setParameter("email", user.getEmail())
                .getSingleResult();

        User updateUser = new User(userOut.getId(), "userNewName", "user@email.ru");
        assertThat(userService.updateUser(UserMapper.toUserDto(updateUser), user.getId()),
                equalTo(UserMapper.toUserDto(updateUser)));
    }
}
