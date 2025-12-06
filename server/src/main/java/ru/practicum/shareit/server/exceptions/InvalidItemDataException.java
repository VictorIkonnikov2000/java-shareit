package ru.practicum.shareit.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidItemDataException extends RuntimeException {
    public InvalidItemDataException(String message) {
        super(message);
    }
}

