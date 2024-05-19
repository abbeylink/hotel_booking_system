package com.abiodunyekeen.hotelbooking.controller;

import com.abiodunyekeen.hotelbooking.request.SessionRequest;
import com.abiodunyekeen.hotelbooking.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SessionRequest createPaymentIntent(@RequestBody SessionRequest data) {
       return paymentService.createPaymentSession(data);
    }
}







