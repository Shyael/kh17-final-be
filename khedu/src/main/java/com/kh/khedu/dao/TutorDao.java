package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;
import com.kh.khedu.vo.tutor.TutorSearchVO;
import com.kh.khedu.vo.tutor.TutorSubjectVO;

public interface TutorDao {

	// ==================== 강사 기본정보 ====================
	int sequence();
	void insert(TutorDto tutorDto);
	TutorDto selectOne(int tutorNo);
	boolean update(TutorDto tutorDto);
	boolean delete(int tutorNo);

	// ==================== 강사 목록 ====================
	//페이지네이션 + 검색
	List<TutorListVO> selectSearchList(TutorSearchVO search);
	//검색조건 전체 개수
	int selectCount(TutorSearchVO search);
	// ==================== 강사 상세 ====================
	TutorDetailVO selectDetail(int tutorNo);

	// ==================== 강사 등록 가능한 직원 목록 ====================
	List<TutorEmployeeVO> selectAvailableEmployeeList();
	
	//파일
	void connect(int tutorNo, int attachNo);
	Integer selectImage(int tutorNo);
	
	
	/* 강의 관련 강사용으로 만든 Dao (필요 시 다른 곳 사용) */
	
	// =========== 강사가 해당 과목을 담당하는 지 확인  =============
	int checkTutorSubject(int employeeNo, int academySubjectNo);
	// =========== 강사 조회  =============
	List<TutorSubjectVO> tutorListBySubject();
}