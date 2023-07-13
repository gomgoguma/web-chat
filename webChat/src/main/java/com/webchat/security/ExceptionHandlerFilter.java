package com.webchat.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtTokenProvider.TokenException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable e) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("err", e.getMessage());

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
