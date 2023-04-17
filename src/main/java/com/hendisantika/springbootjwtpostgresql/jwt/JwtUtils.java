package com.hendisantika.springbootjwtpostgresql.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hendisantika.springbootjwtpostgresql.model.User;
import com.hendisantika.springbootjwtpostgresql.repository.UserRepository;
import com.hendisantika.springbootjwtpostgresql.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.*;

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

    private final KeyPairGenerator keyPairGenerator;
    private final KeyPair keyPair;

    @Autowired
    private UserRepository userRepository;

    public JwtUtils() throws NoSuchAlgorithmException {
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public String generateJwt(Authentication authentication) throws Exception {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String phoneNumber = userPrincipal.getPhoneNumber();
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        userRepository.existsByPhoneNumber(phoneNumber);

        Map<String, String> claims = new HashMap<>();

        claims.put("action", "read");
        claims.put("phoneNumber", userPrincipal.getPhoneNumber());
        claims.put("name", byPhoneNumber.get().getName());
        claims.put("aud", "*");

        JWTCreator.Builder tokenBuilder = JWT.create()
                .withIssuer("https://s.id/hendisantika")
                .withClaim("jti", UUID.randomUUID().toString())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(300)))
                .withIssuedAt(Date.from(Instant.now()));

        claims.entrySet().forEach(action -> tokenBuilder.withClaim(action.getKey(), action.getValue()));
        return tokenBuilder.sign(Algorithm.RSA256(((RSAPublicKey) keyPair.getPublic()), ((RSAPrivateKey) keyPair.getPrivate())));
    }

    public String generateJwtToken(Authentication authentication) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getPhoneNumberFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public boolean isJWTExpired(DecodedJWT decodedJWT) {
        Date expiresAt = decodedJWT.getExpiresAt();
        return expiresAt.before(new Date());
    }
}
