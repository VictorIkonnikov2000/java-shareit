package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // Проверяем, существует ли пользователь с таким email.
        if (isEmailAlreadyExists(userDto.getEmail())) { // Используем метод из UserServiceImpl
            throw new ConflictException("User with this email already exists");
        }
        return userStorage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        UserDto existingUser = userStorage.getUser(userId);
        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        // Проверяем, не пытается ли пользователь обновить email на уже существующий.
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail()) &&
                isEmailAlreadyExists(userDto.getEmail())) { // Используем метод из UserServiceImpl
            throw new ConflictException("Email already exists");
        }

        return userStorage.updateUser(userId, userDto);
    }

    @Override
    public UserDto getUser(Long userId) {
        UserDto user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    // Метод для проверки существования email. В текущей реализации перемещен из UserStorage.
    private boolean isEmailAlreadyExists(String email) {
        // Реализация поиска email по всем пользователям или через отдельный метод в UserStorage (рекомендуется).
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}

