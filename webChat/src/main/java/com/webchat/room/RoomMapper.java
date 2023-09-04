package com.webchat.room;

import com.webchat.room.object.RoomCreateObject;
import com.webchat.room.object.RoomSearchResultObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoomMapper {
    Integer insertRoom(@Param("userId") Integer userId, @Param("roomType") String roomType);
    Integer insertRoomUser(@Param("roomId") Integer roomId, @Param("userIdList") List<Integer> userIdList, @Param("ownId") int ownId);

    List<RoomSearchResultObject> getMyRooms(Integer id);

    Integer deleteRoomUser(@Param("roomId") Integer roomId, @Param("userId")Integer userId);
    int deleteRoom(Integer roomId);

    int getHiddenUserCount(Integer roomId);
    Integer updateUserVisible(Integer roomId);

    List<Integer> getRoomUserList(Integer roomId);

    RoomSearchResultObject getRoom(@Param("roomId") Integer roomId, @Param("userId")Integer userId);

    Integer updateVisibleState(@Param("roomId") Integer roomId, @Param("userId")Integer userId);

    Map<String, Object> validateCreateRoomData(@Param("roomCreateObject") RoomCreateObject roomCreateObject);

    Map<String, Object> validateRoomDeleteData(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
}
