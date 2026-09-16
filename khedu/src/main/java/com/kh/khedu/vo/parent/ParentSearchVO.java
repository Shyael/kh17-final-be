package com.kh.khedu.vo.parent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "학부모 검색 및 페이징용 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class ParentSearchVO extends PaginationVO{
	private String filter;        // "전체", "대기", "승인" 등
    private String searchKeyword; // 이름, 전화번호, 아이디, 자녀이름 검색어
}
