package com.webchat.user.object;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserSignUpObject {
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String username;
    @NotBlank(message = "패스워드는 필수 입력 항목입니다.")
    private String password;
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
}
