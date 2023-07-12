package com.webchat.user;

import com.webchat.security.JwtToken;
import com.webchat.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public List<Map<String, Object>> getUsers() { return userMapper.getUsers(); }

    public ResponseEntity<?> login(String username, String password, HttpServletResponse response) {
        // Authentication 객체 생성
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        }
        catch(Exception e) {
            return new ResponseEntity<>("사용자 정보가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("username", username);
        updateMap.put("refreshToken", token.getRefreshToken());
        userMapper.updateUser(updateMap);

        Cookie accessToken = new Cookie("accessToken", token.getAccessToken());
        accessToken.setDomain("localhost");
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        accessToken.setPath("/");
        accessToken.setMaxAge(60 * 60 * 24);
        response.addCookie(accessToken);

        Cookie refreshToken = new Cookie("refreshToken", token.getRefreshToken());
        refreshToken.setDomain("localhost");
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        refreshToken.setPath("/");
        refreshToken.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(refreshToken);

        return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
    }

    public void join(Map<String, String> joinInfo) {
        joinInfo.put("password", encoder.encode(joinInfo.get("password")));
        userMapper.insertUser(joinInfo);
    }
}
