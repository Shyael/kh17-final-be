package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dao.TutorCareerDao;
import com.kh.khedu.dao.TutorDao;
import com.kh.khedu.dao.TutorSubjectDao;
import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.dto.TutorCareerDto;
import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.dto.TutorSubjectDto;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

@Service
@Transactional
public class TutorServiceImpl implements TutorService {

	@Autowired
	private TutorDao tutorDao;

	@Autowired
	private TutorSubjectDao tutorSubjectDao;

	@Autowired
	private TutorCareerDao tutorCareerDao;

	@Autowired
	private AcademySubjectDao academySubjectDao;


	// ==================== 강사 기본정보 ====================

	@Override
	public void insert(TutorDto tutorDto) {
		int tutorNo = tutorDao.sequence();

		tutorDto.setTutorNo(tutorNo);

		tutorDao.insert(tutorDto);
	}

	@Override
	public List<TutorListVO> selectList() {
		return tutorDao.selectList();
	}

	@Override
	public List<TutorListVO> selectListBySubject(int academySubjectNo) {
		return tutorDao.selectListBySubject(academySubjectNo);
	}

	@Override
	public TutorDetailVO selectDetail(int tutorNo) {

		TutorDetailVO tutorDetail = tutorDao.selectDetail(tutorNo);

		if(tutorDetail == null) {
			return null;
		}

		// 강사 담당과목 조회
		List<TutorSubjectDto> tutorSubjectList =
				tutorSubjectDao.selectList(tutorNo);

		List<String> subjectList = tutorSubjectList.stream()
				.map(tutorSubject -> {

					AcademySubjectDto academySubjectDto =
							academySubjectDao.selectOne(
									tutorSubject.getAcademySubjectNo()
							);

					return academySubjectDto.getAcademySubjectName();

				})
				.toList();

		tutorDetail.setSubjectList(subjectList);


		// 강사 학력/경력 조회
		List<TutorCareerDto> careerList =
				tutorCareerDao.selectList(tutorNo);

		tutorDetail.setCareerList(careerList);


		return tutorDetail;
	}

	@Override
	public TutorDto update(int tutorNo, TutorDto tutorDto) {

		TutorDto findTutorDto = tutorDao.selectOne(tutorNo);

		if(findTutorDto == null) {
			return null;
		}

		tutorDto.setTutorNo(tutorNo);

		tutorDao.update(tutorDto);

		return tutorDto;
	}

	@Override
	public boolean delete(int tutorNo) {
		return tutorDao.delete(tutorNo);
	}

	@Override
	public List<TutorEmployeeVO> selectAvailableEmployeeList() {
		return tutorDao.selectAvailableEmployeeList();
	}


	// ==================== 강사 과목 ====================

	@Override
	public void insertSubject(TutorSubjectDto tutorSubjectDto) {
		int tutorSubjectNo = tutorSubjectDao.sequence();

		tutorSubjectDto.setTutorSubjectNo(tutorSubjectNo);

		tutorSubjectDao.insert(tutorSubjectDto);
	}

	@Override
	public TutorSubjectDto updateSubject(
			int tutorSubjectNo,
			TutorSubjectDto tutorSubjectDto) {

		TutorSubjectDto findTutorSubjectDto =
				tutorSubjectDao.selectOne(tutorSubjectNo);

		if(findTutorSubjectDto == null) {
			return null;
		}

		tutorSubjectDto.setTutorSubjectNo(tutorSubjectNo);

		tutorSubjectDao.update(tutorSubjectDto);

		return tutorSubjectDto;
	}

	@Override
	public boolean deleteSubject(int tutorSubjectNo) {
		return tutorSubjectDao.delete(tutorSubjectNo);
	}


	// ==================== 강사 학력/경력 ====================

	@Override
	public void insertCareer(TutorCareerDto tutorCareerDto) {
		int tutorCareerNo = tutorCareerDao.sequence();

		tutorCareerDto.setTutorCareerNo(tutorCareerNo);

		tutorCareerDao.insert(tutorCareerDto);
	}

	@Override
	public TutorCareerDto updateCareer(
			int tutorCareerNo,
			TutorCareerDto tutorCareerDto) {

		TutorCareerDto findTutorCareerDto =
				tutorCareerDao.selectOne(tutorCareerNo);

		if(findTutorCareerDto == null) {
			return null;
		}

		tutorCareerDto.setTutorCareerNo(tutorCareerNo);

		tutorCareerDao.update(tutorCareerDto);

		return tutorCareerDto;
	}

	@Override
	public boolean deleteCareer(int tutorCareerNo) {
		return tutorCareerDao.delete(tutorCareerNo);
	}

}