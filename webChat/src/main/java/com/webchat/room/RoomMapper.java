package com.webchat.room;

import com.webchat.msg.object.Msg;
import com.webchat.room.object.RoomSearchResultObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {
    Integer insertRoom(@Param("userId") Integer userId, @Param("roomType") String roomType);
    Integer insertRoomUser(@Param("roomId") Integer roomId, @Param("userIdList") List<Integer> userIdList, @Param("ownId") int ownId);

    List<RoomSearchResultObject> getMyRooms(Integer id);

    int deleteRoomUser(@Param("roomId") Integer roomId, @Param("userId")Integer userId);
    int deleteRoom(Integer roomId);

    int getHiddenUserCount(Integer roomId);
    int updateUserVisible(Integer roomId);

    List<Integer> getRoomUserList(Integer roomId);

    RoomSearchResultObject getRoom(@Param("roomId") Integer roomId, @Param("userId")Integer userId);

    String getRoomType(Integer roomId);

    int updateVisibleState(@Param("roomId") Integer roomId, @Param("userId")Integer userId);

    String validateChatData(@Param("msg") Msg msg, @Param("userId") Integer userId);
}
