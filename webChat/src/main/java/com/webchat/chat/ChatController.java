package com.webchat.chat;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/send")
    public ResponseObject<?> sendMessage(@RequestBody @Valid Msg msg, @AuthenticationPrincipal CustomUserDetails principal) {
        return chatService.sendMsg(msg, principal);
    }
}
