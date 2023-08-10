package com.webchat.room;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.security.CustomUserDetails;
import com.webchat.room.object.RoomCreateObject;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("")
    public ResponseObject<?> createRoom(@RequestBody @Valid RoomCreateObject roomCreateObject, @AuthenticationPrincipal CustomUserDetails user) {
        return roomService.createRoom(roomCreateObject, user);
    }

    @GetMapping("")
    public ResponseObject<?> getRooms(@AuthenticationPrincipal CustomUserDetails user) {
        return roomService.getRooms(user);
    }

    @DeleteMapping("")
    public  ResponseObject<?> deleteRoom(@RequestParam (name = "roomId") Integer roomId) {
        return roomService.deleteRoom(roomId);
    }
}
