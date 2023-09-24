package com.webchat.config.kafka;

import com.webchat.msg.object.Msg;
import com.webchat.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final SimpMessagingTemplate template;
    private final RoomMapper roomMapper;

    @KafkaListener(
            // topicPattern 사용 시 파티션 할당 지연 문제
            //topicPattern = "^" + KafkaConstant.KAFKA_TOPIC_PREFIX + "[0-9]+",
            topics = KafkaConstant.KAFKA_TOPIC_ROOM,
            groupId = KafkaConstant.GROUP_ID
    )
    public void listen(Msg msg) {
        List<Integer> userIds = roomMapper.getRoomUserList(msg.getRoomId());
        for(Integer userId : userIds) {
            template.convertAndSend("/topic/user/"+userId, msg);
        }
    }
}
