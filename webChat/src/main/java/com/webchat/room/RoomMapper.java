package com.webchat.room;

import com.webchat.room.object.RoomSearchResultObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {
    Integer insertRoom(Integer id);
    int insertRoomUser(@Param("roomId") Integer roomId, @Param("userIdList") List<Integer> userIdList, @Param("ownId") int ownId);

    List<RoomSearchResultObject> getRooms(Integer id);

    int deleteRoomUser(Integer roomId);
    int deleteRoom(Integer roomId);

    int getHiddenCount(Integer roomId);
    int updateUserVisible(Integer roomId);

    List<Integer> getRoomUserList(Integer roomId);
}
