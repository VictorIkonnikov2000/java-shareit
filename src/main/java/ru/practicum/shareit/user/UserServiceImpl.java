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
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); // Паттерн для валидации email

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || !isValidEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email не может быть пустым или иметь неверный формат");
        }
        try {

            if (isEmailAlreadyExists(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
            return userStorage.createUser(userDto);
        } catch (ConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при создании пользователя", e);
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        try {
            UserDto existingUser = getUser(userId);

            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {

                if (!isValidEmail(userDto.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email имеет неверный формат");
                }
                if (!Objects.equals(userDto.getEmail(), existingUser.getEmail())) {

                    if (isEmailAlreadyExists(userDto.getEmail())) {
                        throw new ConflictException("Email уже занят.");
                    }
                }
            } else {
                // другая логика
            }


            return userStorage.updateUser(userId, userDto);

        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
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

        getUser(userId);
        userStorage.deleteUser(userId);
    }

    /**
     * Проверяет, существует ли пользователь с таким email в хранилище.
     * Улучшает производительность, если userStorage предоставляет прямой метод поиска по email.
     *
     * @param email Email для проверки.
     * @return true, если email уже существует, иначе false.
     */
    private boolean isEmailAlreadyExists(String email) {
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * Проверяет, соответствует ли строка формату email.
     *
     * @param email Строка для проверки.
     * @return true, если строка соответствует формату email, иначе false.
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}



