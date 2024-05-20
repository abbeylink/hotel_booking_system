package com.abiodunyekeen.hotelbooking.service;

import org.springframework.stereotype.Service;

@Service

public class PayPalPayment implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {

    }
}
