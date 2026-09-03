package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.vo.parent.ParentDetailVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;

public interface ParentDao {

	int sequence();
	void insert(ParentDto parentDto);
	ParentStudentVO selectOneRelationByAccountNo(int accountNo);
	ParentDto selectOneByAccountNo(int accountNo);
	
	//studentNo로 학부모 계정 정보 조회
	ParentDetailVO findParentDetailByStudentNo(int studentNo);
	//검색으로 학부모 계정정보 조회
	List<ParentDetailVO> searchParents(String keyword);

}
