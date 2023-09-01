package com.webchat.config.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<T>{
    private int totalCount;
    private T data;
    private String resMsg;
    private Integer resCd;

    private PageInfo pageInfo;

    public void setResErr(String resErr) {
        this.resCd = Integer.parseInt(resErr.split(",")[0]);
        this.resMsg = resErr.split(",")[1];
    }

    public void setPageInfo(Page<?> page) {
        pageInfo = new PageInfo(page);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PageInfo {
        private int pageNum; // 페이지 번호
        private int pageSize; // 한 페이지에 표시되는 최대 데이터 갯수
        private int pageDataCount; // 해당 페이지에 표시된 데이터 갯수
        private int totalPages; // 전체 페이지 갯수
        private boolean hasPrevious;
        private boolean hasNext;

        public PageInfo (Page<?> page) {
            this.pageNum = page.getNumber();
            this.pageSize = page.getSize();
            this.totalPages = page.getTotalPages();
            this.hasPrevious = page.hasPrevious();
            this.hasNext = page.hasNext();
            this.pageDataCount = page.getNumberOfElements();
        }
    }
}
