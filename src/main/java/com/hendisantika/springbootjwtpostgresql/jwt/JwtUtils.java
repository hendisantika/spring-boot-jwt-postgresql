package com.hendisantika.springbootjwtpostgresql.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-jwt-postgresql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 4/16/23
 * Time: 09:48
 * To change this template use File | Settings | File Templates.
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${hendi.app.jwtSecret}")
    private String jwtSecret;

    @Value("${hendi.app.jwtExpirationMs}")
    private int jwtExpirationMs;
}
