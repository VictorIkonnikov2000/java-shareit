package ru.practicum.shareit.gateway.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private String error;
    private List<String> details;

    public ErrorResponse(String error) {
        this.error = error;
        this.details = null;
    }

    public ErrorResponse(String error, List<String> details) {
        this.error = error;
        this.details = details;
    }
}
