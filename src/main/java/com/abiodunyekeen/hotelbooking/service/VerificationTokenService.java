package com.abiodunyekeen.hotelbooking.service;

import com.abiodunyekeen.hotelbooking.model.VerificationToken;
import com.abiodunyekeen.hotelbooking.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken findUserToken(String token){
       return  tokenRepository.findByToken(token);
    }
}
