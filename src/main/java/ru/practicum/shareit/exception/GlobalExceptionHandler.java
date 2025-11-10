package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Глобальный обработчик исключений для всех контроллеров
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class) // Ловим исключения валидации
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // Создаём карту для ошибок

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage()); // Заполняем карту: поле -> сообщение об ошибке
        });

        // Возвращаем 400 Bad Request и описание ошибок
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

