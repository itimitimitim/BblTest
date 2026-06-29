package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("API Testing Demo - UserController")
class ApiTestingDemoTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Nested
    @DisplayName("GET /users - Retrieve All Users")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return 200 OK and a list of users when users exist")
        void shouldReturnUsersWhenExist() {
            User user1 = new User(1L, "Alice", "alice", "alice@example.com", "123456", "alice.com");
            User user2 = new User(2L, "Bob", "bob", "bob@example.com", "789012", "bob.com");
            List<User> mockUsers = Arrays.asList(user1, user2);
            when(userService.getAllUsers()).thenReturn(mockUsers);

            ResponseEntity<List<User>> response = userController.getAllUsers();

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
            assertEquals("Alice", response.getBody().get(0).getName());

            verify(userService).getAllUsers();
        }

        @Test
        @DisplayName("Should return 200 OK and empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsers() {
            // Arrange
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            // Act
            ResponseEntity<List<User>> response = userController.getAllUsers();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());

            // Verify
            verify(userService).getAllUsers();
        }
    }

    @Nested
    @DisplayName("POST /users - Create User")
    class CreateUserTests {

        @Test
        @DisplayName("Should return 201 Created and the saved user when request body is valid")
        void shouldCreateUserWhenValid() {
            // Arrange
            User requestUser = new User(null, "Alice", "alice", "alice@example.com", "123456", "alice.com");
            User savedUser = new User(1L, "Alice", "alice", "alice@example.com", "123456", "alice.com");
            when(userService.createUser(any(User.class))).thenReturn(savedUser);

            // Act
            ResponseEntity<?> response = userController.createUser(requestUser);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertTrue(response.getBody() instanceof User);
            assertEquals(1L, ((User) response.getBody()).getId());

            // Verify
            verify(userService).createUser(requestUser);
        }

        @Test
        @DisplayName("Should return 400 Bad Request and validation message when required fields (email) are missing")
        void shouldReturnBadRequestWhenRequiredFieldsAreMissing() {
            User invalidUser = new User(null, "Alice", "alice", "", "123456", "alice.com");

            // Act
            ResponseEntity<?> response = userController.createUser(invalidUser);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody() instanceof Map);

            Map<?, ?> errorMap = (Map<?, ?>) response.getBody();
            assertEquals("Validation Failed", errorMap.get("error"));
            assertEquals("name, username, and email are required fields and cannot be null or empty", errorMap.get("message"));

            verify(userService, never()).createUser(any());
        }
    }
}
