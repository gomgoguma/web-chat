package com.webchat.msg.object;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "msg")
public class Msg {
    @Id
    private String id;
    private Integer roomId;
    private String userId;
    private String name;
    private String msg;
    private String dtm;
}
