package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // При выбрасывании этого исключения будет возвращен 403 Forbidden
public class ItemForbiddenException extends RuntimeException {
    public ItemForbiddenException(String message) {
        super(message);
    }
}

