package com.webchat.chat;

import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/api/chat")
public class ChatController {

    private final KafkaTemplate<String, Msg> kafkaTemplate;
    private final ChatService chatService;

    @PostMapping(value = "/send")
    public ResponseObject<?> sendMessage(@RequestBody @Valid Msg msg) {
        ResponseObject responseObject = new ResponseObject();
        if (chatService.sendMessage(msg)) {
            log.info("Produce message : " + msg.toString());
            try {
                kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC_PREFIX + msg.getRoomId(), msg).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        responseObject.setResCd(ResponseConstant.OK);
        return responseObject;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public Msg broadcastGroupMessage(@Payload Msg msg) {
        return msg;
    }
}
