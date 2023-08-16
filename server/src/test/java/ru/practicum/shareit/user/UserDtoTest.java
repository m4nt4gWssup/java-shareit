package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Alex", "alex@alex.ru");
    }

    @Test
    void serializeUserDtoToJson() throws Exception {
        assertThat(json.write(userDto)).extractingJsonPathStringValue("$.name").isEqualTo("Alex");
        assertThat(json.write(userDto)).extractingJsonPathStringValue("$.email").isEqualTo("alex@alex.ru");
    }
}
