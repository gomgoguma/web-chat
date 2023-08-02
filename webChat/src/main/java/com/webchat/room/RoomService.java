package com.webchat.room;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.kafka.KafkaUtil;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.room.object.RoomCreateObject;
import com.webchat.room.object.RoomSearchResultObject;
import com.webchat.user.object.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoomService {

    private final RoomMapper roomMapper;

    @Transactional
    public ResponseObject<?> createRoom(RoomCreateObject roomCreateObject, CustomUserDetails user) {
        ResponseObject responseObject = new ResponseObject();

        Integer roomId = roomMapper.insertRoom(user.getUser().getId());
        if(roomId != null) {
            List<Integer> userIdList = roomCreateObject.getUserIdList();
            userIdList.add(user.getUser().getId());
            int count = roomMapper.insertRoomUser(roomId, userIdList);
            if(count > 0) {
                try {
                    KafkaUtil.createTopic("localhost:9092", "room"+roomId, 3, (short) 1);
                    log.warn("Topic " + "room"+roomId + " created successfully.");
                } catch (ExecutionException | InterruptedException e) {
                    log.warn("Error creating topic: " + e.getMessage());
                    responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
                    responseObject.setResMsg("채팅방 생성에 실패하였습니다.");
                    return responseObject;
                }
            }
        }

        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    public ResponseObject<?> getRooms(CustomUserDetails user) {
        ResponseObject responseObject = new ResponseObject();

        User userInfo = user.getUser();
        List<RoomSearchResultObject> roomList = null;
        if(userInfo != null) {
            roomList = roomMapper.getRooms(userInfo.getId());
        }

        if(roomList != null) {
            List<Integer> roomIds = roomList.stream().map(RoomSearchResultObject::getId).collect(Collectors.toList());
            KafkaUtil.initTopics(KafkaConstant.KAFKA_BROKER, roomIds);
        }

        responseObject.setData(roomList);
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }
}
