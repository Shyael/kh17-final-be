package com.kh.khedu.service;

import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dto.AcademyDto;
import com.kh.khedu.dto.AcademyHistoryDto;
import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.vo.academy.AcademyDetailResponseVO;

public interface AcademyService {
	

	// 학원 전체 상세정보 조회
	// 기본정보 + 연혁 목록 + 과목 목록
	AcademyDetailResponseVO selectDetail();
	
	// ==================== 학원정보 ====================
	// 학원 기본정보 등록
	void insert(AcademyDto academyDto);
	
	// 학원 기본정보 수정
	boolean update(AcademyDto academyDto);

	// 학원 기본정보 삭제
	boolean delete();

	// ==================== 학원연혁 ====================
	// 연혁 등록
	void insertHistory(AcademyHistoryDto academyHistoryDto);

	// 연혁 수정
	AcademyHistoryDto updateHistory(int academyHistoryNo, AcademyHistoryDto academyHistoryDto);

	// 연혁 삭제
	boolean deleteHistory(int academyHistoryNo);

	// ==================== 학원과목 ====================
	// 과목 등록
	void insertSubject(AcademySubjectDto academySubjectDto);

	// 과목 수정
	AcademySubjectDto updateSubject(int academySubjectNo, AcademySubjectDto academySubjectDto);

	// 과목 삭제
	boolean deleteSubject(int academySubjectNo);
}