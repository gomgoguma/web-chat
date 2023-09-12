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
    public ResponseObject<?> addRoomUser(@RequestBody @Valid RoomCreateObject roomCreateObject, @AuthenticationPrincipal CustomUserDetails principal) {
        return roomService.addRoomUser(roomCreateObject, principal.getUser());
    }

    @GetMapping("")
    public ResponseObject<?> getRoom(@RequestParam (name = "roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetails principal) {
        return roomService.getRoom(roomId, principal.getUser());
    }

    @GetMapping("my")
    public ResponseObject<?> getMyRooms(@AuthenticationPrincipal CustomUserDetails principal) {
        return roomService.getMyRooms(principal.getUser());
    }

    @DeleteMapping("")
    public  ResponseObject<?> deleteRoom(@RequestParam (name = "roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetails principal) {
        return roomService.deleteRoom(roomId, principal.getUser());
    }
}
