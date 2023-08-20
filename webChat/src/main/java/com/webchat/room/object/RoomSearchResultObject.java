package com.webchat.room.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomSearchResultObject {
    private Integer id;
    private String roomName;
    private String hostId;
    private String createDtm;
    private String recentMsg;
    private String recentMsgDtm;
}
