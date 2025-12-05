package ru.practicum.shareit.gateway.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
