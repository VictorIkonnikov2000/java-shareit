package ru.practicum.shareit.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Valid @RequestBody UserDto userDto) {
        log.info("Updating user with id={}: {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Get user with id={}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user with id={}", userId);
        userClient.deleteUser(userId);
    }
}

