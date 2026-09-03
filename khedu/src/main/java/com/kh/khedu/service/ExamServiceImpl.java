package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.error.TargetNotfoundException;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamDao examDao;
	
	//시험등록
	@Override
	public int insert(ExamDto examDto) {
		//시험 번호 생성
		int examNo = examDao.sequence();
		
		examDto.setExamNo(examNo);
		
		//시험등록
		examDao.insert(examDto);
		
		return examNo;
	}

	//시험 단일 조회
	@Override
	public ExamDto selectOne(int examNo) {
		ExamDto examDto = examDao.selectOne(examNo);
		
		if(examDto == null) {
			throw new TargetNotfoundException();
		}
		
		return examDto;
	}

	//전체 시험 목록 조회
	@Override
	public List<ExamDto> selectList() {
		return examDao.selectList();
	}

	//특정 강의의 시험 목록 조회
	@Override
	public List<ExamDto> selectListByCourse(int courseNo) {
		return examDao.selectListByCourse(courseNo);
	}

	//특정 강사가 등록한 시험 목록 조회
	@Override
	public List<ExamDto> selectListByEmployee(int employeeNo) {
		return examDao.selectListByEmployee(employeeNo);
	}

	//시험 수정
	@Override
	public boolean update(ExamDto examDto) {
		//시험 존재 확인
		ExamDto findExam = examDao.selectOne(examDto.getExamNo());
		
		if(findExam == null) {
			throw new TargetNotfoundException();
		}
		
		return examDao.update(examDto);
	}

	@Override
	public boolean delete(int examNo) {
		//시험 존재 확인
		ExamDto examDto = examDao.selectOne(examNo);
		
		if(examDto == null) {
			throw new TargetNotfoundException();
		}
		
		return examDao.delete(examNo);
	}

}
