package com.webchat.user.object;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserLoginObject {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;
    @NotBlank(message = "패스워드를 입력해주세요.")
    private String password;
}
