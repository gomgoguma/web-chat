package com.webchat.msg;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getMsgs(@RequestParam (name = "roomId") Integer roomId) {
        return msgService.getMsgs(roomId);
    }
}
