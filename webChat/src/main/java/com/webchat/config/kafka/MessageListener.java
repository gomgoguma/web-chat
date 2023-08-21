    package com.webchat.config.kafka;

    import com.webchat.msg.object.Msg;
    import com.webchat.room.object.Room;
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
                topicPattern = "^" + KafkaConstant.KAFKA_TOPIC_PREFIX + "[0-9]+",
                groupId = KafkaConstant.GROUP_ID
        )
        public void listen(Msg msg) {
            log.info("sending via kafka listener..");
            template.convertAndSend("/topic/group/"+msg.getRoomId(), msg);
        }

        @KafkaListener(
                topicPattern = "^" + KafkaConstant.KAFKA_TOPIC_PREFIX + "[0-9]+",
                groupId = KafkaConstant.GROUP_ID
        )
        public void listen(Room room) {
            log.info("sending via kafka listener..");

            if(!room.getUserIds().isEmpty()) {
                for(Integer userId : room.getUserIds()) {
                    template.convertAndSend("/topic/group/user"+userId, room);
                }

            }
        }

    }
