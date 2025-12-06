package ru.practicum.shareit.server.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.exceptions.UserNotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import javax.validation.Valid; // Импорт для валидации
import java.util.List;

/**
 * Контроллер для управления пользователями.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping // Создание пользователя
    public UserDto createUser(@Valid @RequestBody UserDto userDto) { // Добавлена @Valid
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}") // Обновление пользователя
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) { // Добавлена @Valid
        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}") // Получение пользователя по ID
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping // Получение всех пользователей
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        // Можно получить ID пользователя из исключения:
        Long userId = ex.getUserId();
        String message = ex.getMessage();
        // Залогировать, если необходимо
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND); // 404 Not Found
    }
}


