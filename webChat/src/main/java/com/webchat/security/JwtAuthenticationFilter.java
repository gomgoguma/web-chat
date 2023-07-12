package com.webchat.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestMatcher jwtSkipUri = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/user/login"),
            new AntPathRequestMatcher("/api/user/join")
        );

        if(!jwtSkipUri.matches((HttpServletRequest) request)) {
            // 토큰 유효성 검사
            Map<String, String> token = resolveToken((HttpServletRequest) request);
            if (token != null) {
                String accessToken = jwtTokenProvider.validateToken(token);
                if(accessToken != null) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출
    private Map<String, String> resolveToken(HttpServletRequest request) {
        //String bearerToken = request.getHeader("Authorization");
        //String bearerToken = "";

        Map<String, String> token = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if(token.size() >= 2)
                    break;

                if ("accessToken".equals(cookie.getName())) {
                    token.put("accessToken", cookie.getValue());
                }
                else if("refreshToken".equals(cookie.getName())) {
                    token.put("refreshToken", cookie.getValue());
                }
            }
        }
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
//            return bearerToken.substring(7);
//        }
        return null;
    }
}
