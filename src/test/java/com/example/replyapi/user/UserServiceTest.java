package com.example.replyapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceTest {

    private List<User> registeredUsers;
    private UserService userService;

    @BeforeEach
    public void init() {
        registeredUsers = new ArrayList<>();
        userService = new UserService(registeredUsers);
    }

    @Test
    void registerUserShouldReturn201_Success() {
        User user = new User("ruby", "Passw0rd!", "ruby@example.com", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User successfully registered.", response.getBody());
    }

    @Test
    void registerUserShouldReturn400_SpaceInUsername() {
        User user = new User("ruby granger", "Passw0rd!", "ruby@example.com", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn400_PasswordIsNot8Characters() {
        User user = new User("ruby", "Passw0r", "ruby@example.com", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn400_PasswordHasNoUpperCase() {
        User user = new User("ruby", "passw0rd!", "ruby@example.com", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn400_PasswordHasNoNumber() {
        User user = new User("ruby", "Password!", "ruby@example.com", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn400_EmailHasInvalidFormat() {
        User user = new User("ruby", "Passw0rd!", "ruby@", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn400_DobIsNotISO8601Format() {
        User user = new User("ruby", "Passw0rd!", "ruby@example.com", "1990-21-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn400_CardNumberIsNot16Digits() {
        User user = new User("ruby", "Passw0rd!", "ruby@example.com", "1990-21-01", "123456789123456");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserShouldReturn403_UserIsUnderAge() {
        User user = new User("ruby", "Passw0rd!", "ruby@example.com", "2023-01-01", "1234567891234567");
        ResponseEntity<?> response = userService.registerUser(user);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User must be over 18 years old to register.", response.getBody());
    }

    @Test
    void registerUserShouldReturn409_UsernameIsTaken() {
        User user1 = new User("ruby", "Passw0rd!", "ruby@example.com", "1990-01-01", "1234567891234567");
        ResponseEntity<?> response1 = userService.registerUser(user1);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals("User successfully registered.", response1.getBody());

        User user2 = new User("ruby", "Passw0rd!", "rubygranger@example.com", "1990-01-12", "1234567891234569");
        ResponseEntity<?> response2 = userService.registerUser(user2);

        assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());
        assertEquals("Username is taken. Please select a different username.", response2.getBody());
    }

    // Before testing the getRegisteredUsers() method, set up three users.
    public void setUpCards() {
        User user1 = new User("user1", "Passw0rd!", "user1@example.com", "1990-01-01", "1234567890123456");
        User user2 = new User("user2", "Passw0rd!", "user2@example.com", "1990-01-01", null);
        User user3 = new User("user3", "Passw0rd!", "user3@example.com", "1990-01-01", null);

        registeredUsers.add(user1);
        registeredUsers.add(user2);
        registeredUsers.add(user3);

        userService.registerUser(user1);
        userService.registerUser(user2);
        userService.registerUser(user3);
    }

    @Test
    public void getRegisteredUsersShouldReturnAllUsers_EmptyCreditCard() {
        setUpCards();

        ResponseEntity<List<User>> response = userService.getRegisteredUsers("");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(registeredUsers, response.getBody());
        assertEquals(3, registeredUsers.size());
    }

    @Test
    public void getRegisteredUsersShouldReturnUsers_WithCreditCard() {
        setUpCards();

        ResponseEntity<List<User>> response = userService.getRegisteredUsers("Yes");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<User> filteredUsers = response.getBody();

        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.size());
        assertEquals("user1", filteredUsers.get(0).getUsername());
    }

    @Test
    public void getRegisteredUsersShouldReturnUsers_WithoutCreditCard() {
        setUpCards();

        ResponseEntity<List<User>> response = userService.getRegisteredUsers("No");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<User> filteredUsers = response.getBody();

        assertNotNull(filteredUsers);
        assertEquals(2, filteredUsers.size());
        assertEquals("user2", filteredUsers.get(0).getUsername());
    }
}