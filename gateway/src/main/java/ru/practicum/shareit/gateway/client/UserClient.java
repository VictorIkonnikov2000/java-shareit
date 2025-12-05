package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.build(), serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", -1L, userDto); // Исправлено: передаем -1L в качестве userId
    }

    public ResponseEntity<Object> updateUser(long userId, UserDto userDto) {
        return patch("/" + userId, userId, userDto);
    }

    public ResponseEntity<Object> getUser(long userId) {
        return get("/" + userId, null, null);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("", null, null);
    }

    public void deleteUser(long userId) {
        delete("/" + userId, null, null);
    }
}


