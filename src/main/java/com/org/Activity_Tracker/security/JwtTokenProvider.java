package com.org.Activity_Tracker.security;


import com.org.Activity_Tracker.Config;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Config config;

    public JwtTokenProvider(Config config) {
        this.config = config;
    }

    public String generateToken(String username) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + Long.valueOf(config.getJwtExpirationMs()));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, config.getJwtSecret())
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        return Jwts.parser().setSigningKey(config.getJwtSecret()).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(config.getJwtSecret()).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
