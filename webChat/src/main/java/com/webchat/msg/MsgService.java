package com.webchat.msg;

import com.webchat.config.exception.DatabaseUpdateException;
import com.webchat.config.kafka.KafkaConstant;
import com.webchat.config.response.PageResponseObject;
import com.webchat.config.response.ResponseObject;
import com.webchat.config.response.ResponseConstant;
import com.webchat.msg.object.Msg;
import com.webchat.room.RoomMapper;
import com.webchat.user.object.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MsgService {

    private final MsgRespository msgRespository;
    private final MsgMapper msgMapper;
    private final RoomMapper roomMapper;
    private final KafkaTemplate<String, Msg> kafkaTemplate;


    @Transactional
    public ResponseObject<?> sendMsg(Msg msg, User user) {
        ResponseObject responseObject = new ResponseObject();

        try {
            Map<String, Object> result = msgMapper.validateChatData(msg, user.getId()); // 채팅 권한,데이터 검증
            String resErr = (String) result.get("res_err");
            if(!"".equals(resErr)) {
                responseObject.setResErr(resErr);
                return responseObject;
            }

            msgRespository.save(msg); // 채팅 메시지 momgo db 저장

            // 채팅방에 초대되었지만 채팅방이 보이지 않는 사용자 업데이트
            int hiddenUserCount = roomMapper.getHiddenUserCount(msg.getRoomId());
            if(hiddenUserCount > 0
                    && roomMapper.updateUserVisible(msg.getRoomId()) != hiddenUserCount) {
                throw new DatabaseUpdateException("채팅방 사용자 상태 변경 실패");
            }

            kafkaTemplate.send(KafkaConstant.KAFKA_TOPIC_ROOM, msg).get();
            responseObject.setResCd(ResponseConstant.OK);
        } catch (Exception e) {
            log.warn("Exception During Msg Send", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }

        return responseObject;
    }


    @Transactional(readOnly = true)
    public PageResponseObject<?> getMsgs(Integer roomId, Integer pageNum, User user) {
        PageResponseObject responseObject = new PageResponseObject();
        Sort sort = Sort.by(Sort.Direction.DESC, "dtm");
        int pageSize = 15;
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        try {
            Map<String, Object> result = msgMapper.validateMsgSearchData(roomId, user.getId());
            String resErr = (String) result.get("res_err");
            if(!"".equals(resErr)) {
                responseObject.setResErr(resErr);
                return responseObject;
            }

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
            log.warn("Exception During Msg Search", e);
            responseObject.setResCd(ResponseConstant.INTERNAL_SERVER_ERROR);
        }
        return responseObject;
    }
}
