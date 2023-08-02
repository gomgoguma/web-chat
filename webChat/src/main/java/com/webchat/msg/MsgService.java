package com.webchat.msg;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MsgService {

    private final MsgRespository msgRespository;
    public ResponseObject<?> getMsgs(Integer roomId, Integer pageNum) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dtm");
        int pageSize = 15;

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        List<Msg> msgList = msgRespository.findByRoomId(roomId, pageable);

        ResponseObject responseObject = new ResponseObject();
        responseObject.setData(msgList);
        responseObject.setResCd(msgList.isEmpty() ? ResponseConstant.NOT_FOUND : ResponseConstant.OK);
        return responseObject;
    }
}
