package com.webchat.user;

import com.webchat.config.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        return userService.login(loginInfo.get("username"), loginInfo.get("password"), response);
    }

    @PostMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.check(user);
    }
}
