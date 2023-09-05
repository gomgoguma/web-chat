package com.webchat.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        key = Keys.hmacShaKeyFor(secretByteKey);
    }

    public  Cookie createCookie(String tokenName, String token, String domain, int maxAge) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setDomain(domain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public String createAccessToken(String username, String roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("auth", roles)
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 10))

                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String username, String roles) {
        return Jwts.builder()
                .claim("username", username)
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 24 * 7))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
