package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.TutorCareerDto;

public interface TutorCareerDao {

	// 경력/학력 번호 생성
	int sequence();

	// 경력/학력 등록
	void insert(TutorCareerDto tutorCareerDto);

	// 강사별 경력/학력 목록 조회
	List<TutorCareerDto> selectList(int tutorNo);

	// 경력/학력 상세 조회
	TutorCareerDto selectOne(int tutorCareerNo);

	// 경력/학력 수정
	boolean update(TutorCareerDto tutorCareerDto);

	// 경력/학력 삭제
	boolean delete(int tutorCareerNo);
}
