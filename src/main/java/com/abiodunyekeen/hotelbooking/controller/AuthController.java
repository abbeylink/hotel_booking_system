package com.abiodunyekeen.hotelbooking.controller;

import com.abiodunyekeen.hotelbooking.event.RegistrationCompleteEvent;
import com.abiodunyekeen.hotelbooking.event.listner.RegistrationCompleteListner;
import com.abiodunyekeen.hotelbooking.exception.UserAlreadyExistsException;
import com.abiodunyekeen.hotelbooking.model.User;
import com.abiodunyekeen.hotelbooking.model.VerificationToken;
import com.abiodunyekeen.hotelbooking.repository.VerificationTokenRepository;
import com.abiodunyekeen.hotelbooking.request.LoginRequest;
import com.abiodunyekeen.hotelbooking.response.JwtResponse;
import com.abiodunyekeen.hotelbooking.security.jwt.JwtUtils;
import com.abiodunyekeen.hotelbooking.service.IUserService;
import com.abiodunyekeen.hotelbooking.security.user.HotelUserDetails;
import com.abiodunyekeen.hotelbooking.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;




@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ApplicationEventPublisher publisher;
    private final RegistrationCompleteListner eventListner;
    private final HttpServletRequest servletRequest;
    private final VerificationTokenService tokenService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user, final HttpServletRequest httpServletRequest){
        try{
            userService.registerUser(user);
            //Send email
            publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(httpServletRequest)));
            return ResponseEntity.ok("Registration successful! Please check your email to active your account");

        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request){
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        if (!userDetails.isEnabled()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is not activated. Please activate your account before logging in.");
        }
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token){

        String url = applicationUrl(servletRequest)+"verification-token-resend?token="+token;
        VerificationToken verifyToken = tokenService.findUserToken(token);

        if(verifyToken.getUser().isEnabled()){
            return ResponseEntity.ok( "Account has already been verified");
        }
        String  verificationResult = userService.validateToken(token);

        if(verificationResult.equalsIgnoreCase("valid")){
            return ResponseEntity.ok( "Email has been verified successful");
        }

        return ResponseEntity.badRequest().body( "invalid Verification Link <a href=\""+url+"\"> New verification link</a>") ;


    }

    @GetMapping("/verification-token-resend")
    public String resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        VerificationToken verifyToken =  userService.generateNewVerificationToken(oldToken);
        var user = verifyToken.getUser();
        resendVerificationTokenEmail(user,applicationUrl(request),verifyToken);

        return "New Verification has been sent to your Email to active your account";
    }



    private void resendVerificationTokenEmail(User user, String applicationUrl, VerificationToken verifyToken) throws MessagingException, UnsupportedEncodingException {

        String url = applicationUrl+"/verify-email?token="+verifyToken.getToken();
        eventListner.sendVerificationEmail(url);

    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() +":"+request.getServerPort()+ "/auth"+request.getContextPath();
    }
}
