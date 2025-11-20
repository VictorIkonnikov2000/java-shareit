package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public UserDto createUser(UserDto userDto) {

        User user = fromUserDto(userDto);
        user.setId(nextId++);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            return null;
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
