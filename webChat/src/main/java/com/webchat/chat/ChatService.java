package com.webchat.chat;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.response.ResponseObject;
import com.webchat.msg.MsgRespository;
import com.webchat.msg.object.Msg;
import com.webchat.room.RoomMapper;
import com.webchat.user.object.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

    private final KafkaTemplate<String, Msg> kafkaTemplate;
    private final MsgRespository msgRespository;
    private final RoomMapper roomMapper;

    @Transactional
    public ResponseObject<?> sendMsg(Msg msg, User user) {
        ResponseObject responseObject = new ResponseObject();

        try {
            String resErr = roomMapper.validateChatData(msg, user.getId()); // 채팅 권한,데이터 검증
            if(resErr != null) {
                responseObject.setResErr(resErr);
                return responseObject;
            }
            msgRespository.save(msg); // 채팅 메시지 momgo db 저장

            if(roomMapper.getHiddenUserCount(msg.getRoomId()) > 0) { // 채팅방에 초대되었지만 채팅방이 보이지 않는 사용자 업데이트
                roomMapper.updateUserVisible(msg.getRoomId());
            }

            kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC_ROOM, msg).get();

            responseObject.setResCd(ResponseConstant.OK);
        } catch (Exception e) {
            log.warn("Exception During Send Msg", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

        return responseObject;
    }
}
