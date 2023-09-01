package com.webchat.user;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.user.object.UserLoginObject;
import com.webchat.user.object.UserSearchObject;
import org.apache.commons.lang3.StringUtils;
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

    @PostMapping("")
    public ResponseObject<?> join(@RequestBody Map<String, String> joinInfo) {
        return userService.join(joinInfo);
    }

    @PostMapping("/login")
    public ResponseObject<?> login(@Valid @RequestBody UserLoginObject userLoginObject, HttpServletResponse response) {
        return userService.login(userLoginObject, response);
    }

    @PostMapping("/check")
    public ResponseObject<?> check(@AuthenticationPrincipal CustomUserDetails principal) {
        return userService.check(principal.getUser());
    }
}
