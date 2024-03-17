package com.example.replyapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final List<User> registeredUsers; // TODO: Change variable name to registeredUsers

    @Autowired
    public UserService(List<User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    /**
     * Given user details, checks if user can be registered. If valid, user is registered.
     * @param user The user to be registered.
     * @return HTTP status code depending on whether user is registered.
     */
    public ResponseEntity<?> registerUser(User user) {
        if (!isValidUser(user)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (registeredUsers.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            return new ResponseEntity<>("Username is taken. Please select a different username.", HttpStatus.CONFLICT);
        }

        if (LocalDate.parse(user.getDob()).isAfter(LocalDate.now().minusYears(18))) {
            return new ResponseEntity<>("User must be over 18 years old to register.", HttpStatus.FORBIDDEN);
        }

        registeredUsers.add(user);
        return new ResponseEntity<>("User successfully registered.", HttpStatus.CREATED);
    }

    /***
     * @param creditCard Should either be "Yes" to return users with credit card, "No" to return users with no credit card, or empty to return all registered users.
     * @return The HTTP status code and relevant user list depending on the creditCard parameter.
     */
    public ResponseEntity<List<User>> getRegisteredUsers(String creditCard) {
        if (creditCard.isEmpty()) {
            return ResponseEntity.ok(registeredUsers);
        } else {
            List<User> filteredUsers = registeredUsers.stream()
                    .filter(u -> creditCard.equalsIgnoreCase("Yes") && u.getCreditCardNumber() != null ||
                            creditCard.equalsIgnoreCase("No") && u.getCreditCardNumber() == null)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filteredUsers);
        }
    }

    /**
     * Basic validation method to check for:
     * 1. username is alphanumeric
     * 2. password has min length 8, at least one upper case letter and number
     * 3. email is in the correct email format
     * 4. dob is in ISO 8601 format
     * 5. creditCardNumber is either null or has at least 16 digits
     */
    public boolean isValidUser(User user) {
        return user.getUsername().matches("[a-zA-Z0-9]+") &&
                user.getPassword().matches("(?=.*[0-9])(?=.*[A-Z]).{8,}") &&
                user.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") &&
                this.isValidISO8601Date(user.getDob()) &&
                (user.getCreditCardNumber() == null || user.getCreditCardNumber().matches("\\d{16}"));
    }

    public boolean isValidISO8601Date(String dateString) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Method used by PaymentService to validate payments. Checks if the credit card attached to a payment request already exists in the list of registered users.
     * @param creditCardNumber The credit card number to check exists already.
     * @return True if credit card is registered and false if otherwise.
     */
    public boolean isCreditCardRegistered(String creditCardNumber) {
        for (User user : registeredUsers) {
            if (creditCardNumber.equals(user.getCreditCardNumber())) {
                return true;
            }
        }
        return false;
    }
}
