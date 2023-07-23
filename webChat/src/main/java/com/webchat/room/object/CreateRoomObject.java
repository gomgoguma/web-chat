package com.webchat.room.object;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoomObject {
    private List<Integer> userIdList;
}
