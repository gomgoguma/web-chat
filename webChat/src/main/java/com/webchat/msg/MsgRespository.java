package com.webchat.msg;

import com.webchat.msg.object.Msg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MsgRespository extends MongoRepository<Msg, String> {

    List<Msg> findByRoomId(Integer roomId, Pageable pageable);
}
