package com.example.To_Do_List.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Inyecta el secret desde tu application.yaml
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    // Inyecta la expiración desde tu application.yaml
    @Value("${security.jwt.expiration}")
    private long jwtExpirationInMs;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    // Método para generar el token
    public String generateToken(String email){
        long now = System.currentTimeMillis();
        long exp = now + jwtExpirationInMs;

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(getSigningKey())
                .compact();
    }

    // 1. Extrae el email del token
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 2. Comprueba si el token es válido (pertenece al usuario y no ha caducado)
    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    // 3. Comprueba si la fecha de expiración ya pasó
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 4. Abre el token con la clave secreta y extrae los datos del Payload
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
