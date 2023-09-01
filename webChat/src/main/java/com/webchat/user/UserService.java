package com.webchat.user;

import com.webchat.config.jwt.JwtToken;
import com.webchat.config.jwt.JwtTokenProvider;
import com.webchat.config.jwt.JwtUtil;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.user.object.User;
import com.webchat.user.object.UserLoginObject;
import com.webchat.user.object.UserSearchObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public ResponseObject<?> getUsers(UserSearchObject userSearchObject, User user) {
        ResponseObject responseObject = new ResponseObject();
        userSearchObject.setUserId(user.getId());
        try {
            List<Map<String, Object>> userList = userMapper.getUsers(userSearchObject);
            responseObject.setData(userList);
            responseObject.setResCd(userList.isEmpty() ? ResponseConstant.NOT_FOUND : ResponseConstant.OK);
        } catch (Exception e) {
            log.warn("Exception During Search User", e);
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
            log.warn("Exception During Validate User", e);
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

        response.addCookie(JwtUtil.createCookie("accessToken", token.getAccessToken(), "localhost", 60 * 60 * 24));
        response.addCookie(JwtUtil.createCookie("refreshToken", token.getRefreshToken(), "localhost", 60 * 60 * 24 * 30));

        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    @Transactional
    public ResponseObject<?> join(Map<String, String> joinInfo) {
        ResponseObject responseObject = new ResponseObject();
        joinInfo.put("password", encoder.encode(joinInfo.get("password")));

        try {
            // 사용자 검증
            // username 중복 확인
            // String resErr = validateUserJoinData(roomId, user);

            userMapper.insertUser(joinInfo);
            responseObject.setResCd(ResponseConstant.OK);
        } catch(Exception e) {
            log.warn("Exception During Join User", e);
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
}
