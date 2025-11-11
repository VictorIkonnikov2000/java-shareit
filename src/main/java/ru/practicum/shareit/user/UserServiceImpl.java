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
import java.util.regex.Pattern; // Импортируем Pattern для валидации email

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); // Паттерн для валидации email

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Проверка на null и пустоту email теперь дополнена проверкой на валидность формата.
        // Если email не передан, пустой или невалидный, выбрасываем BAD_REQUEST.
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || !isValidEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email не может быть пустым или иметь неверный формат");
        }
        try {
            // Проверяем уникальность email перед созданием пользователя.
            if (isEmailAlreadyExists(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
            return userStorage.createUser(userDto);
        } catch (ConflictException e) { // Перехватываем ConflictException в createUser
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            // Более общее исключение, если что-то пошло не так при работе с хранилищем или других фазах.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при создании пользователя", e);
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        try {
            UserDto existingUser = getUser(userId); // Получаем существующего пользователя или выбрасываем исключение.

            // Если email в DTO для обновления не null, не пуст и отличается от текущего email пользователя
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
                // Добавляем проверку на валидность формата email при обновлении
                if (!isValidEmail(userDto.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email имеет неверный формат");
                }
                if (!Objects.equals(userDto.getEmail(), existingUser.getEmail())) {
                    // Проверяем, не пытается ли пользователь обновить email на уже существующий.
                    if (isEmailAlreadyExists(userDto.getEmail())) {
                        throw new ConflictException("Email уже занят.");
                    }
                }
            } else {
                // Если email в DTO для обновления пустой, null или не меняется,
                // используем существующий email, чтобы не потерять его и не вызвать ошибку валидации.
                // Также, если email в DTO вообще не передан, то его не меняем.
                // В этом случае, если userDto.getEmail() == null, то его не обновляем,
                // если userDto.getEmail().isEmpty(), обрабатываем это как Bad Request, как выше.
                // Если DTO содержит только null для email, это может означать, что поле не должно обновляться,
                // или что его пытаются установить в null, что запрещено.
                // Текущая логика предполагает, что null email в DTO означает "не менять".
                // Здесь можно добавить проверку, если email обязателен и не может быть установлен в null.
            }

            // userDto.getName() == null || userDto.getName().isEmpty()
            // Поле имени необязательно для обновления, если оно не передано.

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
        // Проверяем, существует ли пользователь перед удалением.
        // Если getUser выбрасывает NotFoundException, то это исключение будет далее перехвачено
        // вышестоящими слоями и преобразовано в ResponseStatusException с HttpStatus.NOT_FOUND.
        getUser(userId);
        userStorage.deleteUser(userId);
    }

    /**
     * Проверяет, существует ли пользователь с таким email в хранилище.
     * Улучшает производительность, если userStorage предоставляет прямой метод поиска по email.
     * @param email Email для проверки.
     * @return true, если email уже существует, иначе false.
     */
    private boolean isEmailAlreadyExists(String email) {
        // Если UserStorage поддерживает прямой метод findByEmail, его следует использовать для оптимизации.
        // Например: return userStorage.findByEmail(email).isPresent();
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * Проверяет, соответствует ли строка формату email.
     * @param email Строка для проверки.
     * @return true, если строка соответствует формату email, иначе false.
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}



