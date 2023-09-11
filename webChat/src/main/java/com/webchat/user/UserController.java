package com.webchat.user;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.user.object.UserLoginObject;
import com.webchat.user.object.UserSearchObject;
import com.webchat.user.object.UserSignUpObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseObject<?> getUsers(@RequestParam(name = "excludeOwnYn", required = false) String excludeOwnYn
                                    , @RequestParam(name = "name", required = false) String name
                                    , @AuthenticationPrincipal CustomUserDetails principal) {
        UserSearchObject userSearchObject = new UserSearchObject();
        if(StringUtils.isNotBlank(excludeOwnYn))
            userSearchObject.setExcludeOwnYn(excludeOwnYn);
        if(StringUtils.isNotBlank(name))
            userSearchObject.setName(name);
        return userService.getUsers(userSearchObject, principal.getUser());
    }

    @PostMapping("/signup")
    public ResponseObject<?> signUp(@Valid @RequestBody UserSignUpObject userSignUpObject) {
        return userService.signUp(userSignUpObject);
    }

    @PostMapping("/login")
    public ResponseObject<?> login(@Valid @RequestBody UserLoginObject userLoginObject, HttpServletResponse response) {
        return userService.login(userLoginObject, response);
    }

    @PostMapping("/check")
    public ResponseObject<?> check(@AuthenticationPrincipal CustomUserDetails principal) {
        return userService.check(principal.getUser());
    }

    @PostMapping("/refresh")
    public ResponseObject<?> validateRefreshToken(HttpServletRequest request) {
        return userService.validateRefreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseObject<?> logout(HttpServletRequest request, HttpServletResponse response) {
        return userService.logout(request, response);
    }
}
