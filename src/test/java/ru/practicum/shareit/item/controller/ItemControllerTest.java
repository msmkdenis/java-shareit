package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    ItemDto itemDto;
    ItemResponseDto itemResponseDto;
    Item item;
    User user;
    CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        item = new Item(1, "item1", "description1", true, user, null);
        itemDto = new ItemDto(1, "item1", "description1", true, null);
        itemResponseDto = new ItemResponseDto(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                true, null, null, null);
        commentDto = new CommentDto(1, "comment", "author", LocalDateTime.now());
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).addItem(any(), anyInt());
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updateItem = new ItemDto(
                itemDto.getId(),
                itemDto.getName(),
                "new description",
                true, null);
        when(itemService.updateItem(any(), anyInt(), anyInt())).thenReturn(updateItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateItem.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updateItem.getName()), String.class))
                .andExpect(jsonPath("$.description", is(updateItem.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(updateItem.getAvailable()), Boolean.class));

        verify(itemService, times(1)).updateItem(any(), anyInt(), anyInt());
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(anyInt());
    }

    @Test
    void findById() throws Exception {
        when(itemService.findItemById(anyInt(), anyInt()))
                .thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable()), Boolean.class));

        verify(itemService, times(1)).findItemById(anyInt(), anyInt());
    }

    @Test
    void findAll() throws Exception {
        when(itemService.findAll(anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.getAvailable()), Boolean.class));

        verify(itemService, times(1)).findAll(anyInt());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(any(), anyInt(), anyInt())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));

        verify(itemService, times(1)).addComment(any(), anyInt(), anyInt());
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.search(anyString(), anyInt())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "5")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto.getAvailable())));

        verify(itemService, times(1)).search(anyString(), anyInt());
    }
}
