package com.abiodunyekeen.hotelbooking.repository;

import com.abiodunyekeen.hotelbooking.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken(String token);
}
