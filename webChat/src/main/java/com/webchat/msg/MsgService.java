package com.webchat.msg;

import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.msg.object.Msg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MsgService {

    private final MsgRespository msgRespository;

    @Transactional(readOnly = true)
    public ResponseObject<?> getMsgs(Integer roomId, Integer pageNum) {
        ResponseObject responseObject = new ResponseObject();
        Sort sort = Sort.by(Sort.Direction.DESC, "dtm");
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        try {
            // 메시지 검증
            // 본인의 채팅방인지 확인
            // String resErr = validateMsgSearchData(roomId, user);

            Page<Msg> msgPage = msgRespository.findByRoomId(roomId, pageable);
            long totalCount = msgPage.getTotalElements(); // 전체 결과 수
            responseObject.setTotalCount((int)totalCount);
            responseObject.setPageInfo(msgPage);

            if(totalCount <= 0) {
                responseObject.setResCd(ResponseConstant.NOT_FOUND);
                return responseObject;
            }

            List<Msg> msgList = new ArrayList<>(msgPage.getContent()); // 페이징된 메시지 목록
            Collections.sort(msgList, Comparator.comparing(Msg::getDtm));

            responseObject.setData(msgList);
            responseObject.setResCd(ResponseConstant.OK);
        } catch (Exception e) {
            log.warn("Exception During Search Msg", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }
        return responseObject;
    }
}
