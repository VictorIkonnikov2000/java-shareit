package ru.practicum.shareit.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) //  HTTP 409 Conflict
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}