package com.kh.khedu.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dao.ExamDao;
import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.QuestionManageDetailVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamDao examDao;
	
	@Autowired
	private QuestionDao questionDao;
	
	@Autowired
	private QuestionOptionDao questionOptionDao;
	
	@Autowired
	private AttachDao attachDao;
	
	//공통 메소드
	//권한검사
	private ExamDto checkAuthority(int examNo, int employeeNo, boolean tutor) {
		ExamDto exam = examDao.selectOne(examNo);
		if(exam == null) {
			throw new TargetNotfoundException();
		}
		//강사라면 본인 시험만 접근 가능
		if(tutor && exam.getEmployeeNo() != employeeNo) {
			throw new GetOutException();
		}
		
		return exam;
	}
	
	//공개 검증 메서드
	private void validatePublish(int examNo) {
	    // 시험 문제 조회
	    List<QuestionDto> questionList = questionDao.selectListByExam(examNo);

	    // 문제가 하나도 없는 시험
	    if (questionList == null || questionList.isEmpty()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "문제가 없는 시험은 공개할 수 없습니다."
	        );
	    }
	    for (QuestionDto question : questionList) {
	        List<QuestionOptionDto> optionList =
	                questionOptionDao.selectListByQuestion(
	                        question.getQuestionNo()
	                );
	        // 보기가 없는 문제
	        if (optionList == null || optionList.isEmpty()) {
	            throw new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    question.getQuestionOrder()
	                            + "번 문제에 보기가 없습니다."
	            );
	        }
	        // 정답 개수
	        long answerCount =
	                optionList.stream().filter(option -> "Y".equals(option.getOptionIsAnswer()))
	                        .count();

	        // 단일 선택이므로 정답은 정확히 1개
	        if (answerCount != 1) {
	            throw new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    question.getQuestionOrder()
	                            + "번 문제의 정답은 정확히 1개여야 합니다."
	            );
	        }
	    }
	}
	
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
	
	@Override
	public ExamDetailVO selectDetail(int examNo, int employeeNo, boolean tutor) {
	    // 권한 확인
	    checkAuthority(examNo, employeeNo, tutor);
	    
	    //시험 기본정보 조회
	    ExamDetailVO exam = examDao.selectDetail(examNo);
	    
	    if(exam == null) {
	    	throw new TargetNotfoundException();
	    }
	    
	    //시험 문제 목록 조회
	    List<QuestionDto> questionList = questionDao.selectListByExam(examNo);
	    
	    //최종적으로 ExamDetailVO에 들어갈 문제 목록
	    List<QuestionManageDetailVO> detailList = new ArrayList<>();
	    
	    //문제번호에 각각 보기 목록 조회를 넣어줌
	    for (QuestionDto question : questionList) {
	        // =========================
	        // 보기 목록 조회
	        // =========================
	        List<QuestionOptionDto> optionList = questionOptionDao.selectListByQuestion(question.getQuestionNo());


	        // =========================
	        // 문제 첨부파일 조회
	        // =========================

	        List<Integer> fileNos =questionDao.selectFiles(question.getQuestionNo());

	        List<AttachDto> fileList;

	        if (fileNos == null || fileNos.isEmpty()) {
	            fileList = List.of();
	        }
	        else {
	            fileList = attachDao.selectList(fileNos);
	        }

	        // =========================
	        // 문제 상세 VO 조립
	        // =========================
	        QuestionManageDetailVO detail = 
	        		QuestionManageDetailVO
	        		.builder()
                        .questionNo(question.getQuestionNo())
                        .examNo(question.getExamNo())
                        .questionContent(question.getQuestionContent())
                        .questionScore(question.getQuestionScore())
                        .questionComment(question.getQuestionComment())
                        .questionOrder(question.getQuestionOrder())
                        .optionList(optionList)
                        .fileList(fileList)
	                .build();

	        detailList.add(detail);
	    }


	    // 시험에 문제 목록 추가
	    exam.setQuestionList(detailList);

	    return exam;
	}
	
	//학생용 상세조회
	@Override
	public StudentExamDetailVO selectDetailByStudent(int examNo, int studentNo) {
		StudentExamDetailVO exam = examDao.selectDetailByStudent(examNo, studentNo);
		
		if(exam == null) {
			throw new TargetNotfoundException();
		}
		
		return exam;
	}

	//전체 시험 목록 조회
	@Override
	public List<ExamListVO> selectList() {
		return examDao.selectList();
	}

	//특정 강의의 시험 목록 조회
	@Override
	public List<ExamListVO> selectListByCourse(int courseNo) {
		return examDao.selectListByCourse(courseNo);
	}

	//특정 강사가 등록한 시험 목록 조회
	@Override
	public List<ExamListVO> selectListByEmployee(int employeeNo) {
		return examDao.selectListByEmployee(employeeNo);
	}
	
	@Override
	public List<StudentExamListVO> selectListByStudent(int studentNo) {
		return examDao.selectListByStudent(studentNo);
	}

	//시험 수정
	@Override
	public boolean update(ExamDto examDto, int employeeNo, boolean tutor) {
		 ExamDto beforeExam = checkAuthority(examDto.getExamNo(), employeeNo, tutor);

		 // 작성중 → 공개로 변경하는 경우
	    if ("공개".equals(examDto.getExamStatus()) && !"공개".equals(beforeExam.getExamStatus())) {
	        validatePublish(examDto.getExamNo());
	    }
	    return examDao.update(examDto);
	}

	@Override
	public boolean delete(int examNo, int employeeNo, boolean tutor) {
	    checkAuthority(examNo, employeeNo, tutor);
	    
	    return examDao.delete(examNo);
	}

}
