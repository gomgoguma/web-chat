package com.webchat.room.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Room {
    private Integer id;
    private String roomName;
    private List<Integer> userIds;
}
