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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.Optional;

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
@Tag(name = "User", description = "Endpoints for managing user")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    @Operation(
            summary = "User Login",
            description = "User Login.",
            tags = {"User"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            User.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Bad Request", responseCode = "400",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not Authorize", responseCode = "403",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not found", responseCode = "404",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal error", responseCode = "500"
                    , content = @Content)
    }
    )
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
    @Operation(
            summary = "Sign Up new User",
            description = "Sign Up new User.",
            tags = {"User"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            User.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Bad Request", responseCode = "400",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not Authorize", responseCode = "403",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not found", responseCode = "404",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal error", responseCode = "500"
                    , content = @Content)
    }
    )
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
    @Operation(
            summary = "Get name from User",
            description = "Get name from User.",
            tags = {"User"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            User.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Bad Request", responseCode = "400",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not Authorize", responseCode = "403",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not found", responseCode = "404",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal error", responseCode = "500"
                    , content = @Content)
    }
    )
    public ResponseEntity<?> getName(@Parameter @RequestHeader(name = "authorization") String authorization) throws JSONException {
        AuthToken token = new AuthToken(authorization);

        boolean isJWTExpired = jwtUtils.isJWTExpired(token.getDecodedJWT());
        if (isJWTExpired) {
            return ResponseEntity.ok(new MessageResponse("Token has been expired!"));
        }

        String phoneNumber = token.getPhoneNumber();
        String name = token.getName();
        return ResponseEntity.ok(new MessageResponse("Name: " + name));
    }

    @GetMapping("/update")
    @Operation(
            summary = "Update name of User",
            description = "Update name of User.",
            tags = {"User"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            User.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Bad Request", responseCode = "400",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not Authorize", responseCode = "403",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not found", responseCode = "404",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal error", responseCode = "500"
                    , content = @Content)
    }
    )
    public ResponseEntity<?> updateName(@Parameter @RequestHeader(name = "authorization") String authorization, @RequestParam String name) throws JSONException {
        AuthToken token = new AuthToken(authorization);

        boolean isJWTExpired = jwtUtils.isJWTExpired(token.getDecodedJWT());
        if (isJWTExpired) {
            return ResponseEntity.ok(new MessageResponse("Token has been expired!"));
        }

        String phoneNumber = token.getPhoneNumber();
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        User user = byPhoneNumber.get();
        user.setName(name);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("New Name has been updated successfully!"));
    }
}
