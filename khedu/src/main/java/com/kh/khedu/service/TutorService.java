package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dto.TutorCareerDto;
import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.dto.TutorSubjectDto;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

public interface TutorService {

	// ==================== 강사 기본정보 ====================
	// 강사 등록
	TutorDto insert(
		TutorDto tutorDto,
		MultipartFile image
	) throws IllegalStateException, IOException;

	// 강사 목록 조회
	List<TutorListVO> selectList();

	// 과목별 강사 목록 조회
	List<TutorListVO> selectListBySubject(int academySubjectNo);

	// 강사 상세 조회
	TutorDetailVO selectDetail(int tutorNo);

	// 강사정보 수정
	TutorDto update(
			int tutorNo, 
			TutorDto tutorDto,
			MultipartFile image
			) throws IllegalStateException, IOException;

	// 강사 삭제
	boolean delete(int tutorNo);

	// 강사 등록 가능한 직원 목록 조회
	List<TutorEmployeeVO> selectAvailableEmployeeList();

	// ==================== 강사 과목 ====================
	// 담당과목 등록
	void insertSubject(TutorSubjectDto tutorSubjectDto);

	// 담당과목 수정
	TutorSubjectDto updateSubject(
			int tutorSubjectNo,
			TutorSubjectDto tutorSubjectDto
	);

	// 담당과목 삭제
	boolean deleteSubject(int tutorSubjectNo);

	// ==================== 강사 학력/경력 ====================
	// 학력/경력 등록
	void insertCareer(TutorCareerDto tutorCareerDto);

	// 학력/경력 수정
	TutorCareerDto updateCareer(
			int tutorCareerNo,
			TutorCareerDto tutorCareerDto
	);

	// 학력/경력 삭제
	boolean deleteCareer(int tutorCareerNo);
	
	//이미지 삭제
	void deleteImage(int tutorNo);

}