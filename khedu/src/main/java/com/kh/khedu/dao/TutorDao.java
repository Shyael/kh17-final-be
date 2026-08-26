package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

public interface TutorDao {

	// ==================== 강사 기본정보 ====================
	int sequence();
	void insert(TutorDto tutorDto);
	TutorDto selectOne(int tutorNo);
	boolean update(TutorDto tutorDto);
	boolean delete(int tutorNo);

	// ==================== 강사 목록 ====================
	List<TutorListVO> selectList();
	List<TutorListVO> selectListBySubject(int academySubjectNo);

	// ==================== 강사 상세 ====================
	TutorDetailVO selectDetail(int tutorNo);

	// ==================== 강사 등록 가능한 직원 목록 ====================
	List<TutorEmployeeVO> selectAvailableEmployeeList();

}