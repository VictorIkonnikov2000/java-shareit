package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>(); // Хранилище пользователей
    private Long nextId = 1L; // Генератор ID

    @Override
    public UserDto createUser(UserDto userDto) {
        // Создаем новый объект User и заполняем его данными из UserDto, используя вспомогательный метод
        User user = fromUserDto(userDto);
        user.setId(nextId++); // Устанавливаем уникальный ID
        users.put(user.getId(), user); // Сохраняем пользователя в хранилище
        return UserMapper.toUserDto(user); // Преобразуем User в UserDto и возвращаем
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            return null; // Или выбросить исключение
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        users.put(userId, existingUser);
        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = users.get(userId);
        return (user != null) ? UserMapper.toUserDto(user) : null;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private User fromUserDto(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
