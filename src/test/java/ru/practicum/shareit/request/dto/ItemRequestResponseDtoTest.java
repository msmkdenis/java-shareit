package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestResponseDtoTest {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    private final ItemDto itemDto = new ItemDto(
            1,
            "name",
            "description",
            true,
            null);

    private final ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(
            1,
            "description",
            LocalDateTime.of(2022, 12, 12, 12, 12, 12),
            null);

    @Test
    void ItemRequestResponseDto() throws IOException {

        List<ItemDto> items = new ArrayList<>();
        items.add(itemDto);
        itemRequestResponseDto.setItems(items);

        var res = json.write(itemRequestResponseDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).hasJsonPath("$.items");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestResponseDto.getId());
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestResponseDto.getDescription());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestResponseDto.getCreated().toString());
        assertThat(res).extractingJsonPathArrayValue("$.items").isInstanceOf(ArrayList.class);
        assertThat(res).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getId());
        assertThat(res).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getName());
        assertThat(res).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getDescription());
        assertThat(res).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getAvailable());
        assertThat(res).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestResponseDto.getItems().get(0).getRequestId());
    }
}
