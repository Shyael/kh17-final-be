package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parent.ParentSearchVO;
import com.kh.khedu.vo.parent.ParentUpdateRequestVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;

public interface ParentDao {

	int sequence();
	void insert(ParentDto parentDto);
	ParentStudentVO selectOneRelationByAccountNo(Integer accountNo);
	ParentDto selectOneByAccountNo(Integer accountNo);
	
	//studentNo로 학부모 계정 정보 조회
	List<ParentDetailVO> findParentDetailByStudentNo(Integer studentNo);
	//검색으로 학부모 계정정보 조회
	List<ParentDetailVO> searchParents(String keyword);
	
	// =====================
	// 관리자/직원
	
	// 학부모 검색
	int count(ParentSearchVO searchVO);
	List<ParentDetailVO> list(ParentSearchVO searchVO);
	boolean approveParent(int parentNo);
	
	// 학부모 상세 수정
	ParentDetailVO detail(int parentNo);
	boolean updateAccountInfo(ParentUpdateRequestVO requestVO);
}
