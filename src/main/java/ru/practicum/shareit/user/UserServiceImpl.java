package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Для логирования
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import javax.validation.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j // Добавляем логирование
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Валидация email
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new ValidationException("Email не может быть пустым."); // Используем ValidationException
        }
        if (!isValidEmail(userDto.getEmail())) {
            throw new ValidationException("Email имеет неверный формат."); // Используем ValidationException
        }

        // Проверка на существование email
        if (isEmailAlreadyExists(userDto.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует.");
        }

        // Если все проверки пройдены, создаем пользователя
        log.info("Creating user with email: {}", userDto.getEmail());
        return userStorage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        UserDto existingUser = getUserForUpdateValidation(userId); // Метод для получения пользователя без выбрасывания NotFoundException

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            // Валидация email
            if (!isValidEmail(userDto.getEmail())) {
                throw new ValidationException("Email имеет неверный формат."); // Используем ValidationException
            }

            // Проверка, изменился ли email и не занят ли он другим пользователем
            if (!Objects.equals(userDto.getEmail(), existingUser.getEmail())) {
                if (isEmailAlreadyExists(userDto.getEmail())) {
                    throw new ConflictException("Email уже занят.");
                }
            }
        }
        // Здесь можно добавить обновление имени, если оно не null
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            // Предполагается, что userStorage.updateUser может обновить только те поля, которые переданы в userDto
            // или что вы передадите существующие поля, если они не обновляются.
            // Для простоты, оставим это на userStorage
        }

        log.info("Updating user with ID {}.", userId);
        return userStorage.updateUser(userId, userDto);
    }

    @Override
    public UserDto getUser(Long userId) {
        // Этот метод используется для получения пользователя, если он не найден, выбрасывается NotFoundException
        UserDto user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("User with ID {} not found.", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        return user;
    }

    // Вспомогательный метод для обновления, чтобы не создавать лишний NotFoundException при получении существующего пользователя
    private UserDto getUserForUpdateValidation(Long userId) {
        UserDto user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден для обновления.");
        }
        return user;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUser(Long userId) {
        getUser(userId); // Проверяем, существует ли пользователь, прежде чем удалять
        userStorage.deleteUser(userId);
        log.info("User with ID {} deleted.", userId);
    }

    private boolean isEmailAlreadyExists(String email) {
        // Более эффективный способ: если userStorage имеет метод findByEmail
        // return userStorage.findByEmail(email).isPresent();
        // В противном случае, ваш текущий метод (менее эффективный, но рабочий)
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}




