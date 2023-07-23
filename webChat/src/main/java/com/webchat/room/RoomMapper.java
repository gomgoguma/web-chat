package com.webchat.room;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {
    Integer insertRoom(Integer id);
    int insertRoomUser(@Param("roomId") Integer roomId, @Param("userIdList") List<Integer> userIdList);
}
