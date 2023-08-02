package com.webchat.config.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<T>{
    private int count;
    private T data;
    private String resMsg;
    private Integer resCd;
}
