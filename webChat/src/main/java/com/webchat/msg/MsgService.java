package com.webchat.msg;

import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MsgService {

    private final MsgRespository msgRespository;
    public ResponseEntity<?> getMsgs(Integer roomId) {
        List<Msg> msgList = msgRespository.findByRoomId(roomId);
        return new ResponseEntity<>(msgList, HttpStatus.OK);
    }
}
