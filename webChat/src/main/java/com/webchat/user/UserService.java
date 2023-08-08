package com.webchat.user;

import com.webchat.config.jwt.JwtToken;
import com.webchat.config.jwt.JwtTokenProvider;
import com.webchat.config.jwt.JwtUtil;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.user.object.User;
import com.webchat.user.object.UserLoginObject;
import com.webchat.user.object.UserSearchObject;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public ResponseObject<?> getUsers(UserSearchObject userSearchObject, CustomUserDetails user) {
        User userInfo = user.getUser();
        if(userInfo != null)
            userSearchObject.setUserId(userInfo.getId());

        List<Map<String, Object>> userList = userMapper.getUsers(userSearchObject);
        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(userList);
        responseObject.setResCd(userList.isEmpty() ? ResponseConstant.NOT_FOUND : ResponseConstant.OK);
        return responseObject;
    }

    public ResponseObject<?> login(UserLoginObject userLoginObject, HttpServletResponse response) {
        ResponseObject responseObject = new ResponseObject();

        // Authentication 객체 생성
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginObject.getUsername(), userLoginObject.getPassword());
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        }
        catch(Exception e) {
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

    public void join(Map<String, String> joinInfo) {
        joinInfo.put("password", encoder.encode(joinInfo.get("password")));
        userMapper.insertUser(joinInfo);
    }

    public ResponseObject<?> check(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", customUserDetails.getUser().getUsername());
        userInfo.put("role", customUserDetails.getUser().getRole());
        userInfo.put("email", customUserDetails.getUser().getEmail());
        userInfo.put("name", customUserDetails.getUser().getName());
        userInfo.put("userId", customUserDetails.getUser().getId());

        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(userInfo);
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }
}
