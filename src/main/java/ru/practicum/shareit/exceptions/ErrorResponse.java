package ru.practicum.shareit.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;

    private List<String> details;

    public ErrorResponse(String error) {
        this.error = error;
        this.details = null;
    }
}
