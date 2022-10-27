package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private final CommentDto commentDto = new CommentDto(
            1,
            "text",
            "userName",
            LocalDateTime.of(2022,12,12,12,12,12)
    );

    @Test
    void commentDto() throws IOException {
        var res = json.write(commentDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.text");
        assertThat(res).hasJsonPath("$.authorName");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId());
        assertThat(res).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(res).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().toString());
    }
}
