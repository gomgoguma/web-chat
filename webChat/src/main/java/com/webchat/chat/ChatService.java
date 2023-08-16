package com.webchat.chat;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.response.ResponseConstant;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.msg.MsgRespository;
import com.webchat.msg.object.Msg;
import com.webchat.user.object.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {

    private final KafkaTemplate<String, Msg> kafkaTemplate;
    private final MsgRespository msgRespository;

    @Transactional
    public ResponseObject<?> sendMsg(Msg msg, CustomUserDetails principal) {
        ResponseObject responseObject = new ResponseObject();

        User user = principal.getUser();
        if(user != null || user.getRoomList() != null || Arrays.asList(user.getRoomList().split(",")).contains(msg.getRoomId().toString()) ) {
            try {
                msgRespository.save(msg);
                log.info("Produce message : " + msg);
                kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC_PREFIX + msg.getRoomId(), msg).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            responseObject.setResCd(ResponseConstant.OK);
        }
        else {
            responseObject.setResCd(ResponseConstant.FORBIDDEN);
            responseObject.setResMsg("채팅방 권한이 없습니다.");
        }

        return responseObject;
    }
}
