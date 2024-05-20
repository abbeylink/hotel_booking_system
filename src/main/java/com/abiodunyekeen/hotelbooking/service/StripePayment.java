package com.abiodunyekeen.hotelbooking.service;

import org.springframework.stereotype.Service;

@Service
public class StripePayment implements PaymentProcessor{
    @Override
    public void processPayment(double amount) {

    }
}
