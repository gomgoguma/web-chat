package com.webchat.room;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.kafka.KafkaUtil;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.msg.object.Msg;
import com.webchat.room.object.Room;
import com.webchat.room.object.RoomCreateObject;
import com.webchat.room.object.RoomSearchResultObject;
import com.webchat.user.object.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate template;

    private final KafkaTemplate<Object, Room> kafkaTemplate;

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
                    KafkaUtil.createTopic(KafkaConstant.KAFKA_BROKER, "room"+roomId, 3, (short) 1);
                    log.warn("Topic " + "room"+roomId + " created successfully.");
                } catch (ExecutionException | InterruptedException e) {
                    log.warn("Error creating topic: " + e.getMessage());
                    responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
                    responseObject.setResMsg("채팅방 생성에 실패하였습니다.");
                    return responseObject;
                }
            }

            Room room = new Room();
            room.setId(roomId);
            room.setRoomName("123");
            room.setUserIds(userIdList.stream().filter(item -> !item.equals(user.getUser().getId())).collect(Collectors.toList()));

            try{
                if (KafkaUtil.checkTopicExist(KafkaConstant.KAFKA_BROKER, "KafkaConstant.KAFKA_TOPIC_PREFIX + roomId")) {
                    KafkaUtil.createTopic(KafkaConstant.KAFKA_BROKER, KafkaConstant.KAFKA_TOPIC_PREFIX + roomId, 3, (short) 1);
                    kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC_PREFIX + roomId,  room).get();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    public ResponseObject<List<RoomSearchResultObject>> getRooms(CustomUserDetails user) {
        ResponseObject<List<RoomSearchResultObject>> responseObject = new ResponseObject<>();

        User userInfo = user.getUser();
        List<RoomSearchResultObject> roomList = null;
        if(userInfo != null) {
            roomList = roomMapper.getRooms(userInfo.getId());
        }

        if(roomList != null) {
            List<Integer> roomIds = roomList.stream().map(RoomSearchResultObject::getId).collect(Collectors.toList());
            List<Msg> recentMsgList = getRecentMsgForRooms(roomIds);

            if(!recentMsgList.isEmpty()) {
                int count=0;
                for(Msg msg : recentMsgList) {
                    for(int i=count ; i < roomList.size() && count < recentMsgList.size() ; i++) {
                        if(roomList.get(i).getId().equals(msg.getRoomId())) {
                            roomList.get(i).setRecentMsg(msg.getMsg());
                            roomList.get(i).setRecentMsgDtm(msg.getDtm());
                            count++;
                            break;
                        }
                    }
                }
                roomList.sort((room1, room2) -> room2.getRecentMsgDtm().compareTo(room1.getRecentMsgDtm()));
            }

            KafkaUtil.initTopics(KafkaConstant.KAFKA_BROKER, roomIds);
        }

        responseObject.setData(roomList);
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    public List<Msg> getRecentMsgForRooms(List<Integer> roomIds) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("roomId").in(roomIds)),
            Aggregation.sort(Sort.Direction.DESC, "dtm"),
            Aggregation.group("roomId")
                    .first("msg").as("msg")
                    .first("dtm").as("dtm"), // roomId를 기준으로 그룹핑하고 그 key(roomId)가 _id로 변환됨
            Aggregation.project("msg","dtm","_id").and("_id").as("roomId").andExclude("_id"), // 필드 선택, AS, 제외
            Aggregation.sort(Sort.Direction.ASC, "roomId")
        );

        AggregationResults<Msg> results = mongoTemplate.aggregate(
            aggregation, "msg", Msg.class
        );

        return results.getMappedResults();
    }

    @Transactional
    public ResponseObject<?> deleteRoom(Integer roomId) {
        ResponseObject responseObject = new ResponseObject();

        if( roomMapper.deleteRoomUser(roomId) > 0
                && roomMapper.deleteRoom(roomId) > 0) {
            // windows에서 topic 삭제 이슈 있음
            if(!KafkaUtil.deleteTopic(KafkaConstant.KAFKA_BROKER,"room"+roomId)) {
                throw new RuntimeException("토픽 삭제 실패");
            }
        }
        else {
            responseObject.setResCd(ResponseConstant.NOT_FOUND);
        }
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }
}
