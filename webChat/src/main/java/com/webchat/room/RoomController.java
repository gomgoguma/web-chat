package com.webchat.room;

import com.webchat.config.security.CustomUserDetails;
import com.webchat.room.object.RoomCreateObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("")
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateObject roomCreateObject, @AuthenticationPrincipal CustomUserDetails user) {
        return roomService.createRoom(roomCreateObject, user);
    }

    @GetMapping("")
    public ResponseEntity<?> getRooms(@AuthenticationPrincipal CustomUserDetails user) {
        return roomService.getRooms(user);
    }
}
