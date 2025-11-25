// CommentMapper.java
package ru.practicum.shareit.item; // Или в другом пакете, где у вас лежат мапперы для комментариев

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserRepository; // Если нужно получить имя автора по ID

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentMapper {private final UserRepository userRepository; // Для получения имени автора

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        // Получаем имя автора
        userRepository.findById(comment.getAuthorId())
                .ifPresent(user -> commentDto.setAuthorName(user.getName()));
        return commentDto;
    }

    // Если нужен метод в обратную сторону
    public Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        // authorId установить здесь не можем, так как его нет в CommentDto напрямую
        return comment;
    }

}
