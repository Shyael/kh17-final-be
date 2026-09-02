package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ParentStudentDto;
import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;

public interface ParentStudentDao {
	ParentStudentDto findByParentStudentNo(ParentStudentDto parentStudentDto);

	void insert(ParentStudentDto parentStudentDto);

	List<ParentStudentVO> findByParentNo(int parentNo);
	
	//학부모 번호로 학생이름목록 조회하기
	List<ParentStudentDetailVO> selectStudentListByParentNo(int parentNo);
	
	//학생번호로 학생부모 관계 테이블 조회
	ParentStudentDto findParentStudentByStudentNo(int studentNo);
	
	//학생과 부모의 관계 수정
	boolean updateReltaionship(ParentStudentDto parentStudentDto);
}
