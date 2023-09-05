package com.webchat.config.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        RequestMatcher jwtSkipUri = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/user/login"),
            new AntPathRequestMatcher("/api/user/signup"),
            new AntPathRequestMatcher("/my-chat/**"), // TO-DO : 웹소켓 인증
            new AntPathRequestMatcher("/api/user/refresh")
        );

        if(!jwtSkipUri.matches(request)) {
            // 토큰 유효성 검사
            String token = request.getHeader("Authorization");

            String accessToken = jwtTokenProvider.validateToken(token);
            if(accessToken != null) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출
//    private Map<String, String> resolveToken(HttpServletRequest request) {
//        Map<String, String> token = new HashMap<>();
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null) {
//            for (Cookie cookie : cookies) {
//                if(token.size() >= 2)
//                    break;
//
//                if ("accessToken".equals(cookie.getName())) {
//                    token.put("accessToken", cookie.getValue());
//                }
//                else if("refreshToken".equals(cookie.getName())) {
//                    token.put("refreshToken", cookie.getValue());
//                }
//            }
//        }
//        return token;
//    }
}
