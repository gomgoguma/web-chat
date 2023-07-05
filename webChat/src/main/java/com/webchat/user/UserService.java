package com.webchat.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<Map<String, Object>> getUsers() {
        return userMapper.getUsers();
    }
}
