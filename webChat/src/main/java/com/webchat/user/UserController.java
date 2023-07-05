package com.webchat.user;

import com.webchat.security.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public JwtToken login(@RequestBody Map<String, String> loginInfo) {
        JwtToken jwtToken = userService.login(loginInfo.get("username"), loginInfo.get("password"));
        return jwtToken;
    }

}
