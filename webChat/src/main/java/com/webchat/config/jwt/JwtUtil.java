package com.webchat.config.jwt;

import javax.servlet.http.Cookie;

public class JwtUtil {

    public static Cookie createCookie(String tokenName, String token, String domain, int maxAge) {
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setDomain(domain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
