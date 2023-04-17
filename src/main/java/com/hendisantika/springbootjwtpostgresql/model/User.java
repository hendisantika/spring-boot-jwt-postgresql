package com.hendisantika.springbootjwtpostgresql.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-jwt-postgresql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 4/16/23
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "phoneNumber")
        })
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 10, max = 13)
    @Pattern(regexp = "^08\\d{10,13}$", message = "Phone number mandatory min 10 max 13 must started with 08")
    private String phoneNumber;

    @NotBlank
    @Size(max = 60, message = "Name mandatory max 60")
    private String name;

    @NotBlank
    private String password;

    public User(String phoneNumber, String name, String password) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.password = password;
    }
}
