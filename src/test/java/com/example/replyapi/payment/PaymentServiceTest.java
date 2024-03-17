package com.example.replyapi.payment;

import com.example.replyapi.user.User;
import com.example.replyapi.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;

    // Before each test, register at least one user.
    @BeforeEach
    public void init() {
        List<User> registeredUsers = new ArrayList<>();
        UserService userService = new UserService(registeredUsers);
        List<Payment> payments = new ArrayList<>();
        paymentService = new PaymentService(payments, userService);

        User user = new User("ruby", "Passw0rd!", "ruby@example.com", "1990-01-01", "1234567891234567");
        userService.registerUser(user);
    }

    @Test
    void processPaymentShouldReturn201_Success() {
        Payment payment = new Payment("1234567891234567", 121);
        ResponseEntity<?> response = paymentService.processPayment(payment);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Payment is successful.", response.getBody());
    }

    @Test
    void processPaymentShouldReturn400_CardNumberIsNot16Digits() {
        Payment payment = new Payment("123", 121);
        ResponseEntity<?> response = paymentService.processPayment(payment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void processPaymentShouldReturn400_PaymentAmountIsNot3Digits() {
        Payment payment = new Payment("1234567891234567", 3);
        ResponseEntity<?> response = paymentService.processPayment(payment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void processPaymentShouldReturn404_CardNumberIsNotRegistered() {
        Payment payment = new Payment("1234567891234569", 121);
        ResponseEntity<?> response = paymentService.processPayment(payment);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Credit card is not registered against a registered user.", response.getBody());
    }
}