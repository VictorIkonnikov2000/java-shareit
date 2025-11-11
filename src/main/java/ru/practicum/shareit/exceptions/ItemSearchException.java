package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500 Internal Server Error
public class ItemSearchException extends RuntimeException {

    public ItemSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}