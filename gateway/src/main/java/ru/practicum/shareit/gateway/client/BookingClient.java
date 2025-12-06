package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.dto.BookingDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.build(), serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, Long userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approveBooking(Long bookingId, Boolean approved, Long userId) {
        return patch("/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> getBooking(Long bookingId, Long userId) {
        return get("/" + bookingId, userId, null);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner", userId, parameters);
    }
}

