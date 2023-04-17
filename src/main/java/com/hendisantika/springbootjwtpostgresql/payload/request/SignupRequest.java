package com.hendisantika.springbootjwtpostgresql.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-jwt-postgresql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 4/16/23
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 10, max = 13)
    @Pattern(regexp = "^08\\d{10,13}$", message = "Phone number mandatory min 10 max 13 must started with 08")
    private String phoneNumber;

    @NotBlank
    @Size(max = 60, message = "Name mandatory max 60")
    private String name;

    @NotBlank
    @Size(min = 6, max = 16)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,16}$", message = "Password mandatory min 6, max 16, containing at least 1 capital letter\n" +
            "and 1 number.")
    private String password;
}
