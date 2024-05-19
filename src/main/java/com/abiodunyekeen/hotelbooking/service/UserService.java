package com.abiodunyekeen.hotelbooking.service;

import com.abiodunyekeen.hotelbooking.exception.UserAlreadyExistsException;
import com.abiodunyekeen.hotelbooking.model.Role;
import com.abiodunyekeen.hotelbooking.model.User;
import com.abiodunyekeen.hotelbooking.model.VerificationToken;
import com.abiodunyekeen.hotelbooking.repository.RoleRepository;
import com.abiodunyekeen.hotelbooking.repository.UserRepository;
import com.abiodunyekeen.hotelbooking.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //System.out.println(user.getPassword());
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null){
            userRepository.deleteByEmail(email);
        }

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void saveUserVerificationToken(User user, String verificationToken) {
        var token = new VerificationToken(verificationToken,user);
        verificationTokenRepository.save(token);
    }

    @Override
    public String validateToken(String token) {
        VerificationToken verifyToken = verificationTokenRepository.findByToken(token);
        if (verifyToken == null){
            return "Invalid token";
        }
        User user = verifyToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if (verifyToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            return "Token Already Expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        var verificationTokenTime =new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpirationTime(verificationTokenTime.getTokenExpirationTime());

        return verificationTokenRepository.save(verificationToken);
    }
}
