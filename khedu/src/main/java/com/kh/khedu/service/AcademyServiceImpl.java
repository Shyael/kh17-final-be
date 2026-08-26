package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AcademyDao;
import com.kh.khedu.dao.AcademyHistoryDao;
import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dto.AcademyDto;
import com.kh.khedu.dto.AcademyHistoryDto;
import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.academy.AcademyDetailResponseVO;

@Service
@Transactional
public class AcademyServiceImpl implements AcademyService {

	@Autowired
	private AcademyDao academyDao;

	@Autowired
	private AcademyHistoryDao academyHistoryDao;

	@Autowired
	private AcademySubjectDao academySubjectDao;

	// ==================== 학원정보 ====================

	@Override
	public void insert(AcademyDto academyDto) {
		int academyNo = academyDao.sequence();

		academyDto.setAcademyNo(academyNo);

		academyDao.insert(academyDto);
	}

	@Override
	public AcademyDetailResponseVO selectDetail() {

		// 학원정보는 항상 1개만 존재
		AcademyDto academy = academyDao.selectOne();

		int academyNo = academy.getAcademyNo();

		// 해당 학원의 연혁 조회
		List<AcademyHistoryDto> historyList =
				academyHistoryDao.selectList(academyNo);

		// 해당 학원의 과목 조회
		List<AcademySubjectDto> subjectList =
				academySubjectDao.selectList(academyNo);

		return AcademyDetailResponseVO.builder()
				.academy(academy)
				.historyList(historyList)
				.subjectList(subjectList)
				.build();
	}

	@Override
	public boolean update(AcademyDto academyDto) {

		AcademyDto academy = academyDao.selectOne();

		academyDto.setAcademyNo(academy.getAcademyNo());

		return academyDao.update(academyDto);
	}

	@Override
	public boolean delete() {

		AcademyDto academy = academyDao.selectOne();

		return academyDao.delete(academy.getAcademyNo());
	}

	// ==================== 학원연혁 ====================

	@Override
	public void insertHistory(AcademyHistoryDto academyHistoryDto) {

		AcademyDto academy = academyDao.selectOne();

		int academyHistoryNo = academyHistoryDao.sequence();

		academyHistoryDto.setAcademyHistoryNo(academyHistoryNo);
		academyHistoryDto.setAcademyNo(academy.getAcademyNo());

		academyHistoryDao.insert(academyHistoryDto);
	}

	@Override
	public AcademyHistoryDto updateHistory(
		int academyHistoryNo,
		AcademyHistoryDto academyHistoryDto) {

		AcademyHistoryDto findAcademyHistoryDto = academyHistoryDao.selectOne(academyHistoryNo);

		if(findAcademyHistoryDto == null) {
			throw new TargetNotfoundException();
		}

		academyHistoryDto.setAcademyHistoryNo(academyHistoryNo);

		academyHistoryDao.update(academyHistoryDto);

		return academyHistoryDto;
	}

	@Override
	public boolean deleteHistory(int academyHistoryNo) {
		return academyHistoryDao.delete(academyHistoryNo);
	}

	// ==================== 학원과목 ====================

	@Override
	public void insertSubject(AcademySubjectDto academySubjectDto) {

		AcademyDto academy = academyDao.selectOne();

		int academySubjectNo = academySubjectDao.sequence();

		academySubjectDto.setAcademySubjectNo(academySubjectNo);
		academySubjectDto.setAcademyNo(academy.getAcademyNo());

		academySubjectDao.insert(academySubjectDto);
	}

	@Override
	public AcademySubjectDto updateSubject(
		int academySubjectNo,
		AcademySubjectDto academySubjectDto) {

		AcademySubjectDto findAcademySubjectDto =
				academySubjectDao.selectOne(academySubjectNo);

		if(findAcademySubjectDto == null) {
			throw new TargetNotfoundException();
		}

		academySubjectDto.setAcademySubjectNo(academySubjectNo);

		academySubjectDao.update(academySubjectDto);

		return academySubjectDto;
	}

	@Override
	public boolean deleteSubject(int academySubjectNo) {
		return academySubjectDao.delete(academySubjectNo);
	}

}