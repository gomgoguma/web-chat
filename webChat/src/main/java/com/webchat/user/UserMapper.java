package com.webchat.user;

import com.webchat.user.object.User;
import com.webchat.user.object.UserSearchObject;
import com.webchat.user.object.UserSignUpObject;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    void updateUser(Map<String, String> updateMap);

    List<Map<String, Object>> getUsers(UserSearchObject userSearchObject);
    User getUser(String username);
    Integer insertUser(UserSignUpObject userSignUpObject);
    User validationRefreshToken(String username, String refreshToken);
    Map<String, Object> validateUserSignUpData(UserSignUpObject userSignUpObject);
}
