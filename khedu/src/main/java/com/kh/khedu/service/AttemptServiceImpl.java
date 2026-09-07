package com.kh.khedu.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AttemptDao;
import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dto.AttemptDto;
import com.kh.khedu.error.AlreadyExistsException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.exam.StudentExamDetailVO;

@Service
@Transactional
public class AttemptServiceImpl implements AttemptService {

    @Autowired
    private AttemptDao attemptDao;
    @Autowired
    private ExamDao examDao;

    // 시험 응시 시작
    @Override
    public int insert(AttemptDto attemptDto) {
    	int examNo = attemptDto.getExamNo();
    	int studentNo = attemptDto.getStudentNo();
    	
    	//학생이 접근 가능한 시험인지 확인
    	StudentExamDetailVO exam = 
    			examDao.selectDetailByStudent(examNo, studentNo);
    	
    	if(exam == null) {
    		throw new TargetNotfoundException();
    	}
    	
    	//공개된 시험인지 확인
    	if(!"공개".equals(exam.getExamStatus())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"응시할 수 없는 시험입니다."
    		);
    	}
    	
    	Timestamp now = new Timestamp(System.currentTimeMillis());

    	//시험 시작 전
    	if (now.before(exam.getExamStart())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"아직 시험 응시 시간이 아닙니다."
    		);
    	}
    	
    	//시험 종료
    	if(!now.before(exam.getExamEnd())) {
    		throw new ResponseStatusException(
    				HttpStatus.BAD_REQUEST,
    				"시험 응시 시간이 종료되었습니다."
    		);
    	}
    	
    	//이미 응시한 시험인지 확인
    	AttemptDto findDto = attemptDao.selectOneByExamStudent(examNo, studentNo);

    	if(findDto != null) {
    		throw new AlreadyExistsException();
    	}
        // 응시 번호 생성
        int attemptNo = attemptDao.sequence();
        attemptDto.setAttemptNo(attemptNo);

        // 최초 응시 상태
        attemptDto.setAttemptStatus("응시중");
        // 응시 등록
        attemptDao.insert(attemptDto);

        return attemptNo;
    }

    // 응시 번호로 단일 응시 조회
    @Override
    public AttemptDto selectOne(int attemptNo) {

        AttemptDto attempt = attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        return attempt;
    }

    // 특정 학생의 특정 시험 응시 조회
    @Override
    public AttemptDto selectOneByExamStudent(
            int examNo,
            int studentNo) {

        return attemptDao.selectOneByExamStudent(
                examNo,
                studentNo
        );
    }

    // 특정 시험의 전체 응시 목록 조회
    @Override
    public List<AttemptDto> selectListByExam(int examNo) {

        return attemptDao.selectListByExam(examNo);
    }

    // 특정 학생의 전체 응시 목록 조회
    @Override
    public List<AttemptDto> selectListByStudent(int studentNo) {

        return attemptDao.selectListByStudent(studentNo);
    }

    // 시험 제출
    @Override
    public boolean submit(AttemptDto attemptDto) {

        // 응시 존재 확인
        AttemptDto findAttempt =attemptDao.selectOne(attemptDto.getAttemptNo());

        if (findAttempt == null) {
            throw new TargetNotfoundException();
        }

        return attemptDao.submit(attemptDto);
    }

    // 응시 상태 수정
    @Override
    public boolean updateStatus(AttemptDto attemptDto) {

        // 응시 존재 확인
        AttemptDto findAttempt =attemptDao.selectOne(attemptDto.getAttemptNo());

        if (findAttempt == null) {
            throw new TargetNotfoundException();
        }

        return attemptDao.updateStatus(attemptDto);
    }

    // 응시 삭제
    @Override
    public boolean delete(int attemptNo) {

        // 응시 존재 확인
        AttemptDto attempt =
                attemptDao.selectOne(attemptNo);

        if (attempt == null) {
            throw new TargetNotfoundException();
        }

        return attemptDao.delete(attemptNo);
    }
}