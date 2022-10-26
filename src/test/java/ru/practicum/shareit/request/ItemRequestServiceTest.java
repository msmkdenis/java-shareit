package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    ItemRequestService itemRequestService;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    User user;
    Item item;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User(1, "userName", "user@email.ru");
        item = new Item(1, "item1", "description1", true, user, null);
        itemRequest = new ItemRequest(1, "request", user,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12));
        itemRequestDto = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated());
    }

    @Test
    void addItemRequest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        assertNotNull(itemRequestResponseDto);
        assertEquals(ItemRequestResponseDto.class, itemRequestResponseDto.getClass());
        assertEquals(itemRequest.getId(), itemRequestResponseDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestResponseDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestResponseDto.getCreated());
    }

    @Test
    void addItemRequestWithWrongUserId() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.addItemRequest(999, itemRequestDto));
    }

    @Test
    void findRequestById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequesterId(anyInt())).thenReturn(List.of(item));
        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.findRequestById(
                user.getId(), itemRequest.getId());
        assertNotNull(itemRequestResponseDto);
        assertEquals(ItemRequestResponseDto.class, itemRequestResponseDto.getClass());
        assertEquals(1, itemRequestResponseDto.getItems().size());
        assertEquals(itemRequest.getId(), itemRequestResponseDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestResponseDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestResponseDto.getCreated());
    }

    @Test
    void findAllRequests() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByOtherUsers(anyInt(), any())).thenReturn(List.of(itemRequest));
        List<ItemRequestResponseDto> itemRequestResponseDtoList =
                itemRequestService.findAllRequests(user.getId(), 5, 10);
        assertNotNull(itemRequestResponseDtoList);
        assertEquals(1, itemRequestResponseDtoList.size());
        assertEquals(ItemRequestResponseDto.class, itemRequestResponseDtoList.get(0).getClass());
        assertEquals(itemRequest.getId(), itemRequestResponseDtoList.get(0).getId());
        assertEquals(itemRequest.getDescription(), itemRequestResponseDtoList.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestResponseDtoList.get(0).getCreated());
    }
}
