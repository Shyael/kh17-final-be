package com.kh.khedu.util;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class PaginationVO {
	private int page = 1;
	private int size = 20;
	private int maxSize = 100;
	
	//페이지 번호 세팅 (음수나 0 방어)
	public void setPage(int page) {
		this.page = (page <= 0) ? 1 : page;
	}
	
	//목록 크기 세팅 (비정상 값 방어 및 과도한 요청 제한)
	public void setSize(int size) {
		/*
		 * 0이하 : size = 20 (고정)
		 * 1 ~ 100 : 입력값 그대로
		 * 100초과 : size = 100 (고정)
		*/		
		this.size = size <= 0 ? 20 : Math.min(size, maxSize);		
	}
	
	@Parameter(hidden = true) //Swagger/OpenAPI 문서에서 해당 파라미터를 숨김
	public int getOffset() {
		//건너뛸 데이터 개수
		// page가 2이고 데이터가 20이면 20개를 건너뛴다  
		return (page - 1) * size;
	}
	
	@Parameter(hidden = true)
	public int getBeginRow() {
		// 선택된 페이지의 처음 번호
		return getOffset() + 1;
	}
	
	@Parameter(hidden = true)
	public int getEndRow() {
		// 선택된 페이지의 마지막 번호
		return page * size;
	}
}
