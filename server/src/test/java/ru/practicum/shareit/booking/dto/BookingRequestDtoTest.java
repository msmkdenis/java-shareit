package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestDtoTest {
    @Autowired
    private JacksonTester<BookingRequestDto> json;

    BookingRequestDto bookingRequestDto = new BookingRequestDto(
            1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

    @Test
    void bookingRequestDto() throws IOException {
        var res = json.write(bookingRequestDto);

        assertThat(res).hasJsonPath("$.itemId");
        assertThat(res).hasJsonPath("$.start");
        assertThat(res).hasJsonPath("$.end");
        assertThat(res).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingRequestDto.getItemId());
        assertThat(res).hasJsonPathValue("$.start");
        assertThat(res).hasJsonPathValue("$.end");
    }
}

