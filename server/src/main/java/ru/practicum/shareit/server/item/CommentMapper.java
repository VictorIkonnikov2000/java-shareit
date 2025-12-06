package ru.practicum.shareit.server.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserRepository userRepository;

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        // Получаем имя автора
        userRepository.findById(comment.getAuthorId()) // Используем authorId из Comment
                .ifPresent(user -> commentDto.setAuthorName(user.getName()));
        return commentDto;
    }

    // ИЗМЕНЕНИЕ ЗДЕСЬ:
    public Comment toComment(CommentDto commentDto, Item item, Long authorId) { // Принимаем authorId
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthorId(authorId); // Устанавливаем authorId
        return comment;
    }
}

