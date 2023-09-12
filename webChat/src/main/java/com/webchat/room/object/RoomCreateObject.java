package com.webchat.room.object;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class RoomCreateObject {
    @NotEmpty(message = "대화 상대가 없습니다.")
    private List<Integer> userIdList;

    private Integer roomId;
}
