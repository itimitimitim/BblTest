package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        if (userId < 1) {
            return ResponseEntity.badRequest().build();
        }
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (isInvalid(user)) {
            return ResponseEntity.badRequest().body(createValidationErrorResponse());
        }
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        if (userId < 1) {
            return ResponseEntity.badRequest().build();
        }
        if (isInvalid(userDetails)) {
            return ResponseEntity.badRequest().body(createValidationErrorResponse());
        }
        return userService.updateUser(userId, userDetails)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if (userId < 1) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.deleteUser(userId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isInvalid(User user) {
        return user == null ||
               user.getName() == null || user.getName().trim().isEmpty() ||
               user.getUsername() == null || user.getUsername().trim().isEmpty() ||
               user.getEmail() == null || user.getEmail().trim().isEmpty();
    }

    private Map<String, String> createValidationErrorResponse() {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Failed");
        error.put("message", "name, username, and email are required fields and cannot be null or empty");
        return error;
    }
}
