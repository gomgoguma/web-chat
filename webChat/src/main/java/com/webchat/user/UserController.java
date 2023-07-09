package com.webchat.user;

import com.webchat.security.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<Map<String, Object>> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("")
    public void join(@RequestBody Map<String, String> joinInfo) {
        userService.join(joinInfo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginInfo, HttpServletResponse response) {
        JwtToken token = userService.login(loginInfo.get("username"), loginInfo.get("password"));
        Cookie cookie = new Cookie("accessToken", token.getAccessToken());
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
