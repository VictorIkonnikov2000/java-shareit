package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component // Добавляем аннотацию, чтобы Spring мог создать экземпляр
public class UserMapper {

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}

