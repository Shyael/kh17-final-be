package com.kh.khedu.dao;

import com.kh.khedu.dto.StudentLinkDto;
import com.kh.khedu.vo.studentLink.StudentLinkVO;

public interface StudentLinkDao {
	int sequenceLink(); //등록
	// 만료된 코드 업데이트
	boolean expireStudentLink(int studentNo);
	// DB에 저장
	void insertStudentLink(StudentLinkVO studentLinkVO);
	
	// 받은 연동코드를 입력해서 연동코드에 대한 정보 조회
	StudentLinkDto findByLinkCode(String linkCode);
	
	// 사용된 연동코드 Y처리
	boolean usedLinkCode(int studentLinkNo);
}
