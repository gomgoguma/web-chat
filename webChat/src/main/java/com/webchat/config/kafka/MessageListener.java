package com.webchat.config.kafka;

import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final SimpMessagingTemplate template;

    @KafkaListener(
            // topicPattern 사용 시 파티션 할당 지연 문제
            //topicPattern = "^" + KafkaConstant.KAFKA_TOPIC_PREFIX + "[0-9]+",
            topics = KafkaConstant.KAFKA_TOPIC_ROOM,
            groupId = KafkaConstant.GROUP_ID
    )
    public void listen(Msg msg) {
        log.info("sending via kafka listener..");
        template.convertAndSend("/topic/group/"+msg.getRoomId(), msg);
    }
}
