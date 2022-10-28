package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    Item item;
    User user;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1, "user", "user@mail.ru"));
        item = itemRepository.save(
                new Item(1, "item", "description", true, user, null));
        comment = commentRepository.save(new Comment(1, "text comment", item, user, LocalDateTime.now()));
    }

    @Test
    void findCommentsByItemId() {
        List<Comment> comments = commentRepository.findCommentsByItemId(item.getId());
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
        assertEquals(comment.getText(), comments.get(0).getText());
        assertEquals(comment.getItem().getId(), comments.get(0).getItem().getId());
        assertEquals(comment.getAuthor().getName(), comments.get(0).getAuthor().getName());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
