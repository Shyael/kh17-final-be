package com.kh.khedu.util;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageResponseVO<T> { //<T> 어떤 VO/DTO를 넣을지는 나중에 결정하겠다
	//1. 핵심 데이터
	private List<T> list; //조회된 데이터 목록 
	private int totalCount; // 전체 행 개수 (select count(*))
	private int page; //현재 페이지 번호
	private int size; // 페이지당 항목 수
	
	//2. UI 렌더링용 계산 필드
	private int totalPages; //전체 페이지 개수
	private int startBlock; //화면 하단 페이지 버튼 시작 번호 (예: 1, 11, 21, ...)
	private int endBlock; //화면 하단 페이지 버튼 끝 번호 (예: 10, 20, 30, ...)
	private boolean prev; //[이전] 버튼 활성화 여부
	private boolean next; //[다음] 버튼 활성화 여부
	
	//기본 생성자
	public PageResponseVO() {}
	
	// PaginationVO와 totalCount를 넘겨받아 자동 계산하는 생성자
	public PageResponseVO(List<T> list, int totalCount, PaginationVO pagination) {
		this.list = list;
		this.totalCount = totalCount;
		this.page = pagination.getPage();
		this.size = pagination.getSize();
		
		// 1) 전체 페이지 수 계산 (나머지가 있으면 올림 처리)
		this.totalPages = (int) Math.ceil((double) totalCount / this.size);
		
		// 2) 하단 페이지 네비게이션 블록 크기 (예 : 한 번에 [1] ~ [10] 표시)
		int blockSize = 10;
		// 현재 페이지가 속한 블록의 마지막 페이지 
		// page가 23이면 23/10 = 2.3 -> 올림 -> 3 -> 3*blocksize = 30 
		// 즉, 3번째 블록의 마지막 블록의 숫자를 보여줌 (30)
		this.endBlock = (int) Math.ceil((double) this.page / blockSize) * blockSize;
		// 페이지 블록의 첫번째 블록의 숫자를 보여줌 (21)
		this.startBlock = this.endBlock - blockSize + 1;
		
		//실제 마지막 페이지를 넘지 않도록 보정
		if(this.endBlock > this.totalPages) {
			this.endBlock = this.totalPages;
		}
		// 3) 이전 / 다음 버튼 노출 여부
		this.prev = this.startBlock > 1;
		this.next = this.endBlock < this.totalPages;
		
		//전체 페이지가 10페이지 이하라면 이전/다음 버튼 없음
		if(this.totalPages <= 10) {
			this.prev = false;
			this.next = false;
		}
	}
}
