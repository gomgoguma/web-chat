package com.webchat.user.object;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {
    private String username;
    private String password;
    private String email;
    private String role;
}
