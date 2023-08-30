package com.webchat.room;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.kafka.KafkaUtil;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.msg.object.Msg;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoomService {

    private final RoomMapper roomMapper;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public ResponseObject<?> createRoom(RoomCreateObject roomCreateObject, CustomUserDetails user) {
        ResponseObject responseObject = new ResponseObject();

        int ownId = user.getUser().getId();
        String roomType = roomCreateObject.getUserIdList().size() > 1 ? "G":"P";
        Integer roomId = roomMapper.insertRoom(ownId, roomType);
        if(roomId != null) {
            List<Integer> userIdList = roomCreateObject.getUserIdList();
            userIdList.add(user.getUser().getId());
            roomMapper.insertRoomUser(roomId, userIdList, ownId);
        }

        responseObject.setData(roomId);
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    public ResponseObject<List<RoomSearchResultObject>> getMyRooms(CustomUserDetails user) {
        ResponseObject<List<RoomSearchResultObject>> responseObject = new ResponseObject<>();

        User userInfo = user.getUser();
        List<RoomSearchResultObject> roomList = null;
        if(userInfo != null) {
            roomList = roomMapper.getMyRooms(userInfo.getId());
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
                roomList.sort(Comparator.comparing(RoomSearchResultObject::getRecentMsgDtm, Comparator.nullsLast(Comparator.reverseOrder())));
            }
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

    public ResponseObject<?> getRoom(Integer roomId, CustomUserDetails principal) {
        ResponseObject responseObject = new ResponseObject();

        RoomSearchResultObject roomSearchResultObject = roomMapper.getRoom(roomId, principal.getUser().getId());
        if(roomSearchResultObject == null) {
            responseObject.setResCd(ResponseConstant.NOT_FOUND);
        }
        else {
            responseObject.setData(roomSearchResultObject);
            responseObject.setResCd(ResponseConstant.OK);
        }
        return responseObject;
    }
}
