package com.omsoft.retail.gateway.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.omsoft.retail.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${application.secret.key}")
    private String secret;

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims token(String token) {
        try {
           return extractClaims(token);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid error {}"+e.getMessage());
        }
    }
}
