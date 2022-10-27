package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestInDto() throws IOException {

        User user = new User(1, "userName", "user@email.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", user,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12));

        var res = json.write(itemRequestDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.requester");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId());
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(res).extractingJsonPathNumberValue("$.requester.id")
                .isEqualTo(itemRequestDto.getRequester().getId());
        assertThat(res).extractingJsonPathStringValue("$.requester.name")
                .isEqualTo(itemRequestDto.getRequester().getName());
        assertThat(res).extractingJsonPathStringValue("$.requester.email")
                .isEqualTo(itemRequestDto.getRequester().getEmail());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
    }
}
