package com.kh.khedu.vo.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SseAlarmVO {
    // 알림 종류
    // ASSIGNMENT / EXAM / CONSULT / NOTICE ...
    private String type;

    // 사용자에게 보여줄 메시지
    private String message;

    // 상세 이동에 사용할 대상 번호
    private Integer targetNo;

    // 프론트 이동 주소
    private String targetUrl;
}