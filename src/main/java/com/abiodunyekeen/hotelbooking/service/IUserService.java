package com.abiodunyekeen.hotelbooking.service;

import com.abiodunyekeen.hotelbooking.model.User;
import com.abiodunyekeen.hotelbooking.model.VerificationToken;

import java.util.List;



public interface IUserService {
    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);

    void saveUserVerificationToken(User user, String verificationToken);

    String validateToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);
}
