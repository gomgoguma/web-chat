package com.webchat.chat;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/api/chat")
public class ChatController {

    private final KafkaTemplate<String, Msg> kafkaTemplate;
    private final ChatService chatService;

    @PostMapping(value = "/send")
    public ResponseEntity<?> sendMessage(@RequestBody Msg msg) {
        if (chatService.sendMessage(msg)) {
            log.info("Produce message : " + msg.toString());
            try {
                kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC_PREFIX + msg.getRoomId(), msg).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public Msg broadcastGroupMessage(@Payload Msg msg) {
        return msg;
    }
}
