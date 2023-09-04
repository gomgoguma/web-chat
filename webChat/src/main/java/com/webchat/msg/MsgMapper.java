package com.webchat.msg;

import com.webchat.msg.object.Msg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MsgMapper {

    Map<String, Object> validateChatData(@Param("msg") Msg msg, @Param("userId") Integer userId);
    Map<String, Object> validateMsgSearchData(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
}
