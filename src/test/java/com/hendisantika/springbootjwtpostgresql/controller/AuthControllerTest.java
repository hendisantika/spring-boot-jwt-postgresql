package com.hendisantika.springbootjwtpostgresql.controller;

import com.hendisantika.springbootjwtpostgresql.payload.response.JwtResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * <pre>
 *     com.hendisantika.springbootjwtpostgresql.controller.AuthControllerTest
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 18 Apr 2023 11:19
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(value=MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = this.port;
    }

    @Test
    @Order(1)
    @DisplayName("Login with a wrong credentials")
    public void testWrongLoginPassword() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{\"phoneNumber\":\"123\", \"password\":\"321\"}")
                .log().all()
                .when()
                .post("/api/auth/signin")
                .then()
                .statusCode(401).log().all()
                .body("message", equalTo("Bad credentials"))
                .body("status", equalTo(401));
    }

    @Test
    @Order(2)
    @DisplayName("Register user")
    public void testRegister() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{\"phoneNumber\":\"081655242331\", \"password\":\"Password1\", \"name\":\"edwin\"}")
                .log().all()
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(200).log().all()
                .body("message", equalTo("User registered successfully!"));
    }

    @Test
    @Order(3)
    @DisplayName("Login with a right credentials")
    public void testSuccessLogin() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{\"phoneNumber\":\"081655242331\", \"password\":\"Password1\"}")
                .log().all()
                .when()
                .post("/api/auth/signin")
                .then()
                .statusCode(200).log().all()
                .body("type", equalTo("Bearer"))
                .body("token", is(notNullValue()))
                .body("phoneNumber", equalTo("081655242331"));
    }

    @Test
    @Order(4)
    @DisplayName("Login with a right credentials and accessing protected api")
    public void testSuccessLogin_andSuccessAccessingProtectedAPI() {
        ResponseBody body = given()
                                .accept(ContentType.JSON)
                                .contentType(ContentType.JSON)
                                .body("{\"phoneNumber\":\"081655242331\", \"password\":\"Password1\"}")
                                .post("/api/auth/signin")
                                .getBody();
        JwtResponse response = body.as(JwtResponse.class);

        given()
                .accept(ContentType.JSON)
                .header(new Header("Authorization", "Bearer "+response.getToken()))
                .log().all()
                .when()
                .get("/api/test/user")
                .then()
                .statusCode(200).log().all();
    }


    @Test
    @Order(5)
    @DisplayName("Login with a wrong credentials and accessing protected api")
    public void testFailedLogin_andFailingAccessingProtectedAPI() {
        given()
                .accept(ContentType.JSON)
                .header(new Header("Authorization", "Bearer "+"some-random-token"))
                .log().all()
                .when()
                .get("/api/test/user")
                .then()
                .statusCode(401).log().all();
    }

}
