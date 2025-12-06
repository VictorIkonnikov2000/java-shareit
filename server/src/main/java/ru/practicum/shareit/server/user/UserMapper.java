package ru.practicum.shareit.server.user;

import ru.practicum.shareit.server.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName()); // Можно просто устанавливать напрямую, @NotBlank обеспечит, что в DTO оно не будет пустым.
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        // ***ИЗМЕНЕНИЕ ЗДЕСЬ***
        // Просто устанавливаем значение из сущности. Если user.getName() == null,
        // то userDto.setName(null) корректно установит null.
        userDto.setName(user.getName());
        // Конец изменения
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}

