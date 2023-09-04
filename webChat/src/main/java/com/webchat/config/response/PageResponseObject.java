package com.webchat.config.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
public class PageResponseObject<T> extends ResponseObject<T> {
    private PageInfo pageInfo;

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
