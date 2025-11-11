package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP 400 Bad Request
public class InvalidItemDataException extends RuntimeException {

    public InvalidItemDataException(String message) {
        super(message);
    }
}

