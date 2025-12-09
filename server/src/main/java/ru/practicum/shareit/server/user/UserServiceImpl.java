package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exceptions.ConflictException;
import ru.practicum.shareit.server.exceptions.NotFoundException;
import ru.practicum.shareit.server.exceptions.ValidationException;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;  // Предполагаем, что UserMapper существует и правильно настроен
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        validateUserDto(userDto); // Используем общую валидацию
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User user = userMapper.toEntity(userDto);  // Используем UserMapper
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);  // Используем UserMapper
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) { //Убрал лишнии проверки что есть есть  уже в валидаторах
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        boolean isUpdated = false;

        if (userDto.getName() != null) {
            if (userDto.getName().isBlank()) {
                throw new ValidationException("Name cannot be blank");
            }
            user.setName(userDto.getName());
            isUpdated = true;
        }

        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank()) {
                throw new ValidationException("Email cannot be blank");
            }

            if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
                throw new ValidationException("Email format is invalid");
            }

            if (!userDto.getEmail().equals(user.getEmail())) {
                Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
                if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                    throw new ConflictException("Email " + userDto.getEmail() + " уже используется");
                }
                user.setEmail(userDto.getEmail());
                isUpdated = true;
            }
        }

        if (!isUpdated) {
            throw new ValidationException("Не передано ни одного поля для обновления");
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) { //Переименовываем чтоб не путать
        User user = getUserEntityById(userId);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    private User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    // Вспомогательный метод для ручной валидации UserDto - перенесен
    private void validateUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(userDto.getEmail()).matches()) {
            throw new ValidationException("Email format is invalid");
        }
    }
}
