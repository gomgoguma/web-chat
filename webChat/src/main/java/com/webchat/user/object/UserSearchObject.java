package com.webchat.user.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchObject {
    private String excludeOwnYn;
    private Integer userId;
    private String name;
    private Integer roomId;
    private String excludeExitingUserYn;
}
