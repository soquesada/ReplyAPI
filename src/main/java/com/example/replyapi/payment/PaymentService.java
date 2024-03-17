package com.example.replyapi.payment;

import com.example.replyapi.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final List<Payment> payments;
    private final UserService userService;

    @Autowired
    public PaymentService(List<Payment> payments, UserService userService) {
        this.payments = payments;
        this.userService = userService;
    }

    /**
     * Given payment details, checks if payment can be processed. If valid, payment is processed.
     * @param payment The payment to be processed.
     * @return HTTP status code depending on whether payment is processed.
     */
    public ResponseEntity<?> processPayment(Payment payment) {
        if (!isValidPayment(payment)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String creditCardNumber = payment.getCreditCardNumber();

        if (!userService.isCreditCardRegistered(creditCardNumber)) {
            return new ResponseEntity<>("Credit card is not registered against a registered user.", HttpStatus.NOT_FOUND);
        }

        payments.add(payment);
        return new ResponseEntity<>("Payment is successful.", HttpStatus.CREATED);
    }

    /**
     * Basic validation method to check for:
     * 1. creditCardNumber has 16 digits
     * 2. payment has 3 digits
     */
    private boolean isValidPayment(Payment payment) {
        return payment.getCreditCardNumber().matches("\\d{16}") &&
                payment.getAmount() >= 100 && payment.getAmount() <= 999;
    }
}
