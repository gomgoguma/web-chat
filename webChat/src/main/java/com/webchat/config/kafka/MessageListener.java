package com.webchat.config.kafka;

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
            topics = KafkaConstant.KAFKA_TOPIC,
            groupId = KafkaConstant.GROUP_ID
    )
    public void listen(Message message) {
        log.info("sending via kafka listener..");
        template.convertAndSend("/topic/group", message);
    }
}
