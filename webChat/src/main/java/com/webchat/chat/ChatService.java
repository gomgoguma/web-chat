package com.webchat.chat;

import com.webchat.msg.MsgRespository;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final MsgRespository msgRespository;

    @Transactional
    public boolean sendMessage(Msg msg) {
        msgRespository.save(msg);
        return true;
    }
}
