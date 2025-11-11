package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Это исключение будет выбрасываться, когда пользователь пытается выполнить
// действие над предметом, но у него нет на это достаточных прав (например,
// пытается обновить чужой предмет).
// Аннотация @ResponseStatus гарантирует, что при выбрасывании этого исключения
// Spring вернет HTTP-код 403 FORBIDDEN.
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ItemForbiddenException extends RuntimeException {
    public ItemForbiddenException(String message) {
        super(message);
    }
}
