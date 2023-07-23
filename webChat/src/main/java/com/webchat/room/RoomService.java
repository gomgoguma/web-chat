package com.webchat.room;

import com.webchat.config.kafka.KafkaUtil;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.room.object.CreateRoomObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoomService {

    private final RoomMapper roomMapper;

    @Transactional
    public ResponseEntity<?> createRoom(CreateRoomObject createRoomObject, CustomUserDetails user) {
        Integer roomId = roomMapper.insertRoom(user.getUser().getId());
        if(roomId != null) {
            List<Integer> userIdList = createRoomObject.getUserIdList();
            userIdList.add(user.getUser().getId());
            int count = roomMapper.insertRoomUser(roomId, userIdList);
            if(count > 0) {
                try {
                    KafkaUtil.createTopic("localhost:9092", "room"+roomId, 3, (short) 1);
                    log.warn("Topic " + "room"+roomId + " created successfully.");
                } catch (ExecutionException | InterruptedException e) {
                    log.warn("Error creating topic: " + e.getMessage());
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
