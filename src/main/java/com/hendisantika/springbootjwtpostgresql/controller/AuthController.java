package com.hendisantika.springbootjwtpostgresql.controller;

import com.hendisantika.springbootjwtpostgresql.jwt.JwtUtils;
import com.hendisantika.springbootjwtpostgresql.model.User;
import com.hendisantika.springbootjwtpostgresql.payload.AuthToken;
import com.hendisantika.springbootjwtpostgresql.payload.request.LoginRequest;
import com.hendisantika.springbootjwtpostgresql.payload.request.SignupRequest;
import com.hendisantika.springbootjwtpostgresql.payload.response.JwtResponse;
import com.hendisantika.springbootjwtpostgresql.payload.response.MessageResponse;
import com.hendisantika.springbootjwtpostgresql.repository.UserRepository;
import com.hendisantika.springbootjwtpostgresql.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-jwt-postgresql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 4/16/23
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwt(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getPhoneNumber()
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone Number is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getPhoneNumber(), signUpRequest.getName(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/name")
    public ResponseEntity<?> getName(@RequestHeader(name = "authorization") String authorization) throws JSONException {
        AuthToken token = new AuthToken(authorization);

        boolean isJWTExpired = jwtUtils.isJWTExpired(token.getDecodedJWT());
        if (isJWTExpired) {
            return ResponseEntity.ok(new MessageResponse("Token has been expired!"));
        }

        String phoneNumber = token.getPhoneNumber();
        String name = token.getName();
        return ResponseEntity.ok(new MessageResponse("Name: " + name));
    }


}
