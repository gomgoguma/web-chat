package com.webchat.msg;

import com.webchat.msg.object.Msg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MsgRespository extends MongoRepository<Msg, String> {

    Page<Msg> findByRoomId(Integer roomId, Pageable pageable);
}
