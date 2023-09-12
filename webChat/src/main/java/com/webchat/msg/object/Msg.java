package com.webchat.msg.object;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Document(collection = "msg")
public class Msg {
    @Id
    private String id;
    @NotNull
    @Min(1)
    private Integer roomId;
    @NotNull
    private Integer userId;
    private String name;
    @NotEmpty
    private String msg;
    private String dtm;
    private String type;
}
