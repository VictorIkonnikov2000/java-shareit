package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.build(), serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getItemRequestsByUserId(Long userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> getAllItemRequests(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all", userId, parameters);
    }

    public ResponseEntity<Object> getItemRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId, null);
    }
}
