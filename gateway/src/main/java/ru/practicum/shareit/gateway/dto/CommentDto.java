package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст не может быть пустым")
    @Size(min = 1, max = 500)
    private String text;

    private String authorName;
    private LocalDateTime created;
}
