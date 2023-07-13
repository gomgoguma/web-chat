package com.webchat.security;

import com.webchat.user.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final UserMapper userMapper;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserMapper userMapper) {
        this.userMapper = userMapper;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails u = (CustomUserDetails) authentication.getPrincipal();
        //Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token 생성
        String refreshToken = Jwts.builder()
                .claim("username", u.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 24 * 15))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public String validateToken(Map<String, String> token, HttpServletResponse response) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.get("accessToken"));
            return token.get("accessToken");
        }catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new TokenException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.get("refreshToken"));

                Claims claims = parseClaims(token.get("refreshToken"));
                if (claims.get("username") == null) {
                    throw new Exception("유효하지 않은 토큰입니다.");
                }

                com.webchat.user.object.User user = userMapper.validationRefreshToken((String) claims.get("username"), token.get("refreshToken"));
                if(user != null) {
                    String accessToken = Jwts.builder()
                            .setSubject(user.getUsername())
                            .claim("auth", user.getRole())
                            .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 30))
                            .signWith(key, SignatureAlgorithm.HS256)
                            .compact();

                    Cookie cookie = new Cookie("accessToken", accessToken);
                    cookie.setDomain("localhost");
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 60 * 24);
                    response.addCookie(cookie);

                    return accessToken;
                }
                else{
                    throw new Exception("유효하지 않은 토큰입니다.");
                }
            }
            catch (Exception expired) {
                log.info("Expired JWT Token", e);
                throw new TokenException("만료된 토큰입니다.");
            }
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new TokenException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new TokenException("잘못된 토큰입니다.");
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public static class TokenException extends JwtException {
        public TokenException(String message) {
            super(message);
        }
    }

}