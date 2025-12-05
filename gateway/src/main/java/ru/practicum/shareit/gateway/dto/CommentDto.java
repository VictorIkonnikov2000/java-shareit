package ru.practicum.shareit.gateway.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 2048, message = "Текст комментария не может быть больше 2048 символов")
    private String text;

    private String authorName;
    private LocalDateTime created;
}
