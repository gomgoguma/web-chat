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
    private int totalCount;
    private T data;
    private String resMsg;
    private Integer resCd;

    public void setResErr(String resErr) {
        this.resCd = Integer.parseInt(resErr.split(",")[0]);
        this.resMsg = resErr.split(",")[1];
    }
}
