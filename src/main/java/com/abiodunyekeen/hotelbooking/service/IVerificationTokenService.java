package com.abiodunyekeen.hotelbooking.service;

import com.abiodunyekeen.hotelbooking.model.VerificationToken;

public interface IVerificationTokenService {
    VerificationToken findUserToken(String token);
}
