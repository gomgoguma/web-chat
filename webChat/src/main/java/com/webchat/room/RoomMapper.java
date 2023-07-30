package com.webchat.room;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoomMapper {
    Integer insertRoom(Integer id);
    int insertRoomUser(@Param("roomId") Integer roomId, @Param("userIdList") List<Integer> userIdList);

    List<Map<String, Object>> getRooms(Integer id);
}
