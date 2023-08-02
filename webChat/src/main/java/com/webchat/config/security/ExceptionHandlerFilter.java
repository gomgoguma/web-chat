package com.webchat.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webchat.config.jwt.JwtTokenProvider;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            setErrorResponse(HttpStatus.OK, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable e) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        ResponseObject responseObject = new ResponseObject();
        responseObject.setResMsg(e.getMessage());
        responseObject.setResCd(ResponseConstant.UNAUTHORIZED);
        String json = objectMapper.writeValueAsString(responseObject);
        response.getWriter().write(json);
    }
}
