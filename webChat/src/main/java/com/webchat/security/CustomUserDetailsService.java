package com.webchat.security;

import com.webchat.user.UserMapper;
import com.webchat.user.object.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.getUser(username);
        if(user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return new CustomUserDetails(user);
    }
}