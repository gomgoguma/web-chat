package com.webchat.room;

import com.webchat.config.security.CustomUserDetails;
import com.webchat.room.object.CreateRoomObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomObject createRoomObject, @AuthenticationPrincipal CustomUserDetails user) {
        return roomService.createRoom(createRoomObject, user);
    }
}
