package com.webchat.user;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.user.object.UserLoginObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseObject<?> getUsers(@RequestParam(name = "excludeOwnYn", required = false) String excludeOwnYn, @AuthenticationPrincipal CustomUserDetails user) {
        return userService.getUsers(excludeOwnYn, user);
    }

    @PostMapping("")
    public void join(@RequestBody Map<String, String> joinInfo) {
        userService.join(joinInfo);
    }

    @PostMapping("/login")
    public ResponseObject<?> login(@Valid @RequestBody UserLoginObject userLoginObject, HttpServletResponse response) {
        return userService.login(userLoginObject, response);
    }

    @PostMapping("/check")
    public ResponseObject<?> check(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.check(user);
    }
}
