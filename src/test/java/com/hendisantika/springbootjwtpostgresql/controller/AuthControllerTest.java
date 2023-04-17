package com.hendisantika.springbootjwtpostgresql.controller;

import com.hendisantika.springbootjwtpostgresql.jwt.JwtUtils;
import com.hendisantika.springbootjwtpostgresql.model.User;
import com.hendisantika.springbootjwtpostgresql.payload.request.SignupRequest;
import com.hendisantika.springbootjwtpostgresql.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-jwt-postgresql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 4/17/23
 * Time: 19:42
 * To change this template use File | Settings | File Templates.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerTest {
    @Container
    private static final PostgreSQLContainer database = new PostgreSQLContainer("postgres:15.2-alpine3.17");


    private final String body = "{\n" +
            "    \"phoneNumber\": \"081321411800\",\n" +
            "    \"name\": \"Uzumaki Naruto\",\n" +
            "    \"password\": \"Naruto2023!\"\n" +
            "}";
    private final String token = "";

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

//    @BeforeEach
//    public void setup() {
//        // Register the user
//        given()
//                .contentType(ContentType.JSON)
//                .body(body)
//                .when()
//                .post("http://localhost:8080/api/auth/signup")
//                .then()
//                .assertThat()
//                .statusCode(HttpStatus.SC_OK);
//
//        //Generate the token
//        token = given()
//                .contentType(ContentType.JSON)
//                .body(body)
//                .when()
//                .post("http://localhost:8080/api/auth/signin")
//                .jsonPath()
//                .get("token");
//
//    }

    @Test
    void authenticateUser() {
    }

    @Test
    void registerUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setPhoneNumber("081321411800");
        signupRequest.setName("Uzumaki Naruto");
        signupRequest.setPassword("Naruto2023!");

        // Create new user's account
        User user = new User(signupRequest.getPhoneNumber(), signupRequest.getName(),
                encoder.encode(signupRequest.getPassword()));

        userRepository.save(user);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"phoneNumber\": \"081321411800\",\n" +
                                "    \"name\": \"Uzumaki Naruto\",\n" +
                                "    \"password\": \"Naruto2023!\"\n" +
                                "}")

                )
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User registered successfully!")));
    }

    @Test
    void getName() {
    }

    @Test
    void updateName() {
    }
}
