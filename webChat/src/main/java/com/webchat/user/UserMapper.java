package com.webchat.user;

import com.webchat.user.object.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    List<Map<String, Object>> getUsers();
    User getUser(String username);
    void insertUser(Map<String, String> joinInfo);
}
