package com.webchat.room;

import com.webchat.config.response.ResponseConstant;
import com.webchat.config.response.ResponseObject;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoomService {

    private final RoomMapper roomMapper;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public ResponseObject<?> createRoom(RoomCreateObject roomCreateObject, User user) {
        ResponseObject responseObject = new ResponseObject();

        int ownId = user.getId();
        String roomType = roomCreateObject.getUserIdList().size() > 1 ? "G":"P";

        try {
            Map<String, Object> result = roomMapper.validateCreateRoomData(roomCreateObject);
            String resErr = (String) result.get("res_err");
            if(!"".equals(resErr)) {
                responseObject.setResErr(resErr);
                return responseObject;
            }

            Integer roomId = Objects.requireNonNull(roomMapper.insertRoom(ownId, roomType), "채팅방 생성 실패");
            List<Integer> userIdList = roomCreateObject.getUserIdList();
            userIdList.add(user.getId());
            Objects.requireNonNull(roomMapper.insertRoomUser(roomId, userIdList, ownId), "채팅방 사용자 추가 실패");

            responseObject.setResCd(ResponseConstant.OK);
            responseObject.setData(roomId);
        } catch (Exception e) {
            log.warn("Exception During Room Create", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

        return responseObject;
    }

    @Transactional(readOnly = true)
    public ResponseObject<List<RoomSearchResultObject>> getMyRooms(User user) {
        ResponseObject<List<RoomSearchResultObject>> responseObject = new ResponseObject<>();

        try {
            List<RoomSearchResultObject> roomList = roomMapper.getMyRooms(user.getId());

            if(roomList.isEmpty()) {
                responseObject.setResCd(ResponseConstant.NOT_FOUND);
            }
            else {
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
                responseObject.setResCd(ResponseConstant.OK);
                responseObject.setData(roomList);
            }
        } catch (Exception e) {
            log.warn("Exception During MyRoom Search", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

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
    public ResponseObject<?> deleteRoom(Integer roomId, User user) {
        ResponseObject responseObject = new ResponseObject();
        
        try {
            Map<String, Object> result = roomMapper.validateRoomDeleteData(roomId, user.getId());
            String resErr = (String) result.get("res_err");
            if(!"".equals(resErr)) {
                responseObject.setResErr(resErr);
                return responseObject;
            }

            String roomType = (String)result.get("room_type");
            if("P".equals(roomType)) { // 1대1
                // 채팅방 사용자 숨김
                Objects.requireNonNull(roomMapper.updateVisibleState(roomId, user.getId()), "채팅방 사용자 상태 변경 실패");
            }
            else if("G".equals(roomType)) { // 그룹 채팅
                // 채팅방 사용자 삭제
                Objects.requireNonNull(roomMapper.deleteRoomUser(roomId, user.getId()), "채팅방 사용자 삭제 실패");
            }

            responseObject.setResCd(ResponseConstant.OK);
        } catch (Exception e) {
            log.warn("Exception During Room Delete", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

        return responseObject;
    }

    @Transactional(readOnly = true)
    public ResponseObject<?> getRoom(Integer roomId, User user) {
        ResponseObject responseObject = new ResponseObject();

        try {
            RoomSearchResultObject roomSearchResultObject = roomMapper.getRoom(roomId, user.getId());
            if(roomSearchResultObject == null) {
                responseObject.setResCd(ResponseConstant.NOT_FOUND);
            }
            else {
                responseObject.setResCd(ResponseConstant.OK);
                responseObject.setData(roomSearchResultObject);
            }
        } catch (Exception e) {
            log.warn("Exception During Room Search", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

        return responseObject;
    }
}
