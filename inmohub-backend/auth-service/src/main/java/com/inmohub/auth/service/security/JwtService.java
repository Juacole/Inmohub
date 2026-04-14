package com.inmohub.auth.service.security;

import com.inmohub.auth.service.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {
        var roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .toList();

        return Jwts.builder()
                .subject(user.getEmail()) // identificador principal
                .claim("userId", user.getId().toString())
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSingInKey(), Jwts.SIG.HS256) // firma del token con algoritmo HS256
                .compact();
    }

    /**
     * Transforma la clave secreta en una llave criptográfica.
     * Esta llave se utilizara para firmar los tokens generados.
     */
    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
