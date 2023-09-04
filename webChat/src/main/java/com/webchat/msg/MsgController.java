package com.webchat.msg;

import com.webchat.config.response.PageResponseObject;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/msg")
public class MsgController {

    private final MsgService msgService;

    @GetMapping("")
    public PageResponseObject<?> getMsgs(@RequestParam (name = "roomId") Integer roomId,
                                         @RequestParam (name = "pageNum") Integer pageNum,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        return msgService.getMsgs(roomId, pageNum, principal.getUser());
    }

    @PostMapping(value = "/send")
    public ResponseObject<?> sendMessage(@RequestBody @Valid Msg msg, @AuthenticationPrincipal CustomUserDetails principal) {
        return msgService.sendMsg(msg, principal.getUser());
    }
}
