package com.webchat.msg;

import com.webchat.config.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/msg")
public class MsgController {

    private final MsgService msgService;

    @GetMapping("")
    public ResponseObject<?> getMsgs(@RequestParam (name = "roomId") Integer roomId,
                                     @RequestParam (name = "pageNum") Integer pageNum) {
        return msgService.getMsgs(roomId, pageNum);
    }
}
