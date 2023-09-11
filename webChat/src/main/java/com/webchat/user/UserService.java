package com.webchat.user;

import com.webchat.config.exception.DatabaseUpdateException;
import com.webchat.config.jwt.JwtToken;
import com.webchat.config.jwt.JwtTokenProvider;
import com.webchat.config.jwt.JwtUtil;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.user.object.User;
import com.webchat.user.object.UserLoginObject;
import com.webchat.user.object.UserSearchObject;
import com.webchat.user.object.UserSignUpObject;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public ResponseObject<?> getUsers(UserSearchObject userSearchObject, User user) {
        ResponseObject responseObject = new ResponseObject();
        userSearchObject.setUserId(user.getId());
        try {
            List<Map<String, Object>> userList = userMapper.getUsers(userSearchObject);
            responseObject.setData(userList);
            responseObject.setResCd(userList.isEmpty() ? ResponseConstant.NOT_FOUND : ResponseConstant.OK);
        } catch (Exception e) {
            log.warn("Exception During User Search", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

        return responseObject;
    }

    @Transactional
    public ResponseObject<?> login(UserLoginObject userLoginObject, HttpServletResponse response) {
        ResponseObject responseObject = new ResponseObject();

        // Authentication 객체 생성
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginObject.getUsername(), userLoginObject.getPassword());
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        }
        catch(Exception e) {
            log.warn("Exception During User Validation", e);
            responseObject.setResMsg("사용자 정보가 일치하지 않습니다.");
            responseObject.setResCd(ResponseConstant.UNAUTHORIZED);
            return responseObject;
        }

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("username", userLoginObject.getUsername());
        updateMap.put("refreshToken", token.getRefreshToken());
        userMapper.updateUser(updateMap);

        response.addCookie(jwtUtil.createCookie("refreshToken", token.getRefreshToken(), "localhost", 60 * 60 * 24 * 7));

        responseObject.setData(token.getAccessToken());
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    @Transactional
    public ResponseObject<?> signUp(UserSignUpObject userSignUpObject) {
        ResponseObject responseObject = new ResponseObject();
        userSignUpObject.setPassword(encoder.encode(userSignUpObject.getPassword()));

        try {
            Map<String, Object> result = userMapper.validateUserSignUpData(userSignUpObject);
            String resErr = (String) result.get("res_err");
            if(!"".equals(resErr)) {
                responseObject.setResErr(resErr);
                return responseObject;
            }

            if(userMapper.insertUser(userSignUpObject) <= 0) {
                throw new DatabaseUpdateException("사용자 등록 실패");
            }

            responseObject.setResCd(ResponseConstant.OK);
        } catch(Exception e) {
            log.warn("Exception During User Sign-up", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }
        return responseObject;
    }

    public ResponseObject<?> check(User user) {
        ResponseObject responseObject = new ResponseObject();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("userId", user.getId());

        responseObject.setData(userInfo);
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    @Transactional(readOnly = true)
    public ResponseObject<String> validateRefreshToken(HttpServletRequest request) {
        ResponseObject responseObject = new ResponseObject();

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if(refreshToken == null) {
            responseObject.setResCd(ResponseConstant.BAD_REQUEST);
            responseObject.setResMsg("토큰 없음");
        }
        else {
            try {
                String validateToken = jwtTokenProvider.validateToken(refreshToken);
                Claims claims = jwtTokenProvider.parseClaims(validateToken);

                String username = Objects.requireNonNull((String) claims.get("username"), "유효하지 않은 토큰");
                User user = Objects.requireNonNull(userMapper.validationRefreshToken(username, refreshToken), "토큰 없음)");

                String newAccessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());

                // Refresh Token Rotation
                // 리프레시 토큰도 재발급하여 db 업데이트, 쿠키 저장 다시하기
                // 보안이 강화되지만 서버 부하 증가 > 비동기 처리 고려하기

                responseObject.setData(newAccessToken);
                responseObject.setResCd(ResponseConstant.OK);;
            } catch (Exception e) {
                log.warn("Exception During Refresh token Validation", e);
                responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
            }
        }

        return responseObject;
    }

    public ResponseObject<?> logout(HttpServletRequest request, HttpServletResponse response) {
        ResponseObject responseObject = new ResponseObject();

        Cookie jwtDeleteCookie = new Cookie("refreshToken", null);
        jwtDeleteCookie.setMaxAge(0);
        jwtDeleteCookie.setPath("/");
        response.addCookie(jwtDeleteCookie);

        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }
}
