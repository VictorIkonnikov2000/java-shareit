package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email не может быть пустым");
        }
        try {
            // Проверяем уникальность email перед созданием пользователя.
            if (isEmailAlreadyExists(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
            return userStorage.createUser(userDto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при создании пользователя", e);
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        try {
            UserDto existingUser = getUser(userId); // Получаем существующего пользователя или выбрасываем исключение.

            // Проверяем, не пытается ли пользователь обновить email на уже существующий (исключая себя).
            if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), existingUser.getEmail()) &&
                    isEmailAlreadyExists(userDto.getEmail())) {
                throw new ConflictException("Email уже занят.");
            }
            return userStorage.updateUser(userId, userDto);

        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }  catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        UserDto user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUser(Long userId) {
        getUser(userId); // Проверяем, существует ли пользователь перед удалением (или выбрасываем исключение).
        userStorage.deleteUser(userId);
    }

    private boolean isEmailAlreadyExists(String email) {
        // Проверяем, существует ли email в хранилище (можно оптимизировать, если UserStorage поддерживает поиск по email).
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}


