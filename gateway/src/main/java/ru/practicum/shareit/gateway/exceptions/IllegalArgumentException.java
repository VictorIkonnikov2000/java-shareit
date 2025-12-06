package ru.practicum.shareit.gateway.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalArgumentException extends RuntimeException {
    public IllegalArgumentException(String message) {
        super(message);
    }
}

