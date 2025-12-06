package ru.practicum.shareit.gateway.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidItemDataException extends RuntimeException {
    public InvalidItemDataException(String message) {
        super(message);
    }
}
