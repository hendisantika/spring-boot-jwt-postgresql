package com.hendisantika.springbootjwtpostgresql.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
