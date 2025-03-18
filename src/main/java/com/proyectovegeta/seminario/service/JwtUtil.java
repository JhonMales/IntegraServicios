package com.proyectovegeta.seminario.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String instanceSecret;

    @Value("${jwt.expiration}")
    private String instanceExpirationTime;

    private static String secret;
    private static String expirationTime;

    @PostConstruct
    public void init() {
        secret = instanceSecret;
        expirationTime = instanceExpirationTime;
    }

    //JWT Generation
    public String generateToken(String id, String rol) {
        return Jwts.builder()
                .subject(id)
                .claim("rol", rol)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(expirationTime)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    //JWT Decoding
    public  Map<String, String> decodeToken(String token) {
        Claims claims =  Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        //Check if its expired
        if (claims.getExpiration().before(new Date(System.currentTimeMillis()))) {
            throw new RuntimeException("Token expired");
        }
        Map<String, String> result = new HashMap<>();
        result.put("id", claims.getSubject());
        result.put("rol", claims.get("rol", String.class));
        return result;
    }
}