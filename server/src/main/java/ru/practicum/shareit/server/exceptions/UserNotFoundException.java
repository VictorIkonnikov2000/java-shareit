package ru.practicum.shareit.server.exceptions;

public class UserNotFoundException extends RuntimeException {

    private Long userId;

    public UserNotFoundException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public Long getUserId() {
        return userId;
    }

}