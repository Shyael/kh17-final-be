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
import com.kh.khedu.vo.exam.ExamAttemptListVO;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamDraftRequestVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.ExamStatisticsVO;
import com.kh.khedu.vo.exam.QuestionDraftVO;
import com.kh.khedu.vo.exam.QuestionManageDetailVO;
import com.kh.khedu.vo.exam.QuestionOptionDraftVO;
import com.kh.khedu.vo.exam.QuestionStatisticsVO;
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
	
	@Autowired
	private AttachService attachService;
	
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
	
	private void checkStatusChange(
	        String beforeStatus,
	        String afterStatus) {

	    // 같은 상태 유지
	    if (beforeStatus.equals(afterStatus)) {
	        return;
	    }

	    // 작성중 → 공개
	    if ("작성중".equals(beforeStatus) && "공개".equals(afterStatus)) {
	        return;
	    }

	    // 공개 → 마감
	    if ("공개".equals(beforeStatus) && "마감".equals(afterStatus)) {
	        return;
	    }

	    throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "변경할 수 없는 시험 상태입니다."
	    );
	}
	
	//시험등록
	@Override
	public int insert(ExamDto examDto) {
		//시험 번호 생성
		int examNo = examDao.sequence();
		
		examDto.setExamNo(examNo);
		
		//최초 등록은 무조건 작성중
		examDto.setExamStatus("작성중");
		
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
		
		//상태 변경 검증
		checkStatusChange(beforeExam.getExamStatus(), examDto.getExamStatus());
		
		// 작성중 → 공개
	    if ("작성중".equals(beforeExam.getExamStatus()) && "공개".equals(examDto.getExamStatus())) {
	        validatePublish(examDto.getExamNo());
	    }
	    return examDao.update(examDto);
	}

	@Override
	public boolean delete(int examNo, int employeeNo, boolean tutor) {
	    ExamDto exam = checkAuthority(examNo, employeeNo, tutor);
	    
	    // 작성중 시험만 삭제
	    if (!"작성중".equals(exam.getExamStatus())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "공개된 시험은 삭제할 수 없습니다."
	        );
	    }
	    //문제 첨부파일 번호 미리 조회
	    List<QuestionDto> questionList = questionDao.selectListByExam(examNo);
	    List<Integer> fileNos = new ArrayList<>();
	    
	    for (QuestionDto question : questionList) {
	        List<Integer> questionFileNos = questionDao.selectFiles(question.getQuestionNo());

	        if (questionFileNos != null) {
	        	fileNos.addAll(questionFileNos);
	        }
	    }
	    
	    //시험 삭제
	    boolean result = examDao.delete(examNo);
	    
	    //실제 첨부파일 삭제
	    if(result) {
	    	for(Integer attachNo : fileNos) {
	    		attachService.delete(attachNo);
	    	}
	    }
	    
	    return result;
	}

	@Override
	public List<ExamAttemptListVO> selectAttemptList(int examNo, int employeeNo, boolean tutor) {
		//시험 존재 여부 + 강사 본인 시험인지 확인
		checkAuthority(examNo, employeeNo, tutor);
		
		return examDao.selectAttemptList(examNo);
	}
	
	//일괄 저장 + 수정 + 삭제 + 문제 첨부파일 정리
	@Override
	@Transactional
	public ExamDraftRequestVO saveDraft(
	        int examNo,
	        ExamDraftRequestVO request,
	        int employeeNo,
	        boolean tutor) {
	    //==================================================
	    // 1. 시험 권한 확인
	    //==================================================
	    checkAuthority(examNo, employeeNo, tutor);

	    ExamDto exam = examDao.selectOne(examNo);

	    if (exam == null) {
	        throw new TargetNotfoundException();
	    }

	    //작성중 시험만 임시저장 가능
	    if (!"작성중".equals(exam.getExamStatus())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "작성중인 시험만 임시저장할 수 있습니다."
	        );
	    }

	    //==================================================
	    // 2. 요청 데이터 기본 검증
	    //==================================================
	    if (request.getQuestionList() != null) {

	        //문제 순서 중복 확인
	        long questionOrderCount =
	                request.getQuestionList()
	                        .stream()
	                        .map(QuestionDraftVO::getQuestionOrder)
	                        .distinct()
	                        .count();

	        if (questionOrderCount != request.getQuestionList().size()) {
	            throw new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "문제 순서가 중복되었습니다."
	            );
	        }


	        for (QuestionDraftVO questionVO : request.getQuestionList()) {

	            if (questionVO.getOptionList() == null) {
	                continue;
	            }

	            //보기 순서 중복 확인
	            long optionOrderCount =
	                    questionVO.getOptionList()
	                            .stream()
	                            .map(QuestionOptionDraftVO::getOptionOrder)
	                            .distinct()
	                            .count();

	            if (optionOrderCount != questionVO.getOptionList().size()) {
	                throw new ResponseStatusException(
	                        HttpStatus.BAD_REQUEST,
	                        "보기 순서가 중복되었습니다."
	                );
	            }

	            //작성중에는 정답이 없어도 되지만
	            //정답이 2개 이상이면 안 됨
	            long answerCount = questionVO.getOptionList()
	                            .stream()
	                            .filter(option ->
	                                    "Y".equals(
	                                            option.getOptionIsAnswer()
	                                    )
	                            )
	                            .count();

	            if (answerCount > 1) {
	                throw new ResponseStatusException(
	                        HttpStatus.BAD_REQUEST,
	                        "한 문제에는 하나의 정답만 설정할 수 있습니다."
	                );
	            }
	        }
	    }


	    //==================================================
	    // 3. 삭제된 보기 처리
	    //==================================================
	    if (request.getDeletedOptionNos() != null) {
	        for (Integer optionNo : request.getDeletedOptionNos()) {

	            if (optionNo == null) {
	                continue;
	            }

	            QuestionOptionDto option = questionOptionDao.selectOne(optionNo);

	            //이미 없는 데이터면 넘어감
	            if (option == null) {
	                continue;
	            }

	            QuestionDto question = questionDao.selectOne(option.getQuestionNo());

	            if (question == null) {
	                throw new TargetNotfoundException();
	            }

	            //다른 시험의 보기 삭제 방지
	            if (question.getExamNo() != examNo) {
	                throw new GetOutException();
	            }

	            questionOptionDao.delete(optionNo);
	        }
	    }


	    //==================================================
	    // 4. 삭제된 문제 처리
	    //==================================================
		 if (request.getDeletedQuestionNos() != null) {
		     for (Integer questionNo : request.getDeletedQuestionNos()) {
	
		         if (questionNo == null) {
		             continue;
		         }
	
		         QuestionDto question = questionDao.selectOne(questionNo);
	
		         //이미 삭제된 문제면 넘어감
		         if (question == null) {
		             continue;
		         }
	
		         //다른 시험의 문제 삭제 방지
		         if (question.getExamNo() != examNo) {
		             throw new GetOutException();
		         }
	
		         //문제 삭제 전에 첨부파일 번호 조회
		         List<Integer> fileNos = questionDao.selectFiles(questionNo);
	
		         //문제 삭제
		         boolean result = questionDao.delete(questionNo);
	
		         //문제 삭제 성공 시
		         //attach DB + 실제 파일 삭제
		         if (result && fileNos != null) {
		             for (Integer attachNo : fileNos) {
		                 attachService.delete(attachNo);
		             }
		         }
		     }
		 }

	    //문제 목록이 없으면 삭제까지만 처리 후 종료
	    if (request.getQuestionList() == null) {
	        return request;
	    }

	    //==================================================
	    // 5. 문제 저장
	    //==================================================
	    for (QuestionDraftVO questionVO : request.getQuestionList()) {
	        Integer questionNo = questionVO.getQuestionNo();

	        //================================================
	        // 신규 문제
	        //================================================
	        if (questionNo == null) {

	            int newQuestionNo =
	                    questionDao.sequence();

	            QuestionDto questionDto =
	                    QuestionDto.builder()
	                            .questionNo(newQuestionNo)
	                            .examNo(examNo)
	                            .questionContent(
	                                    questionVO.getQuestionContent()
	                            )
	                            .questionScore(
	                                    questionVO.getQuestionScore()
	                            )
	                            .questionComment(
	                                    questionVO.getQuestionComment()
	                            )
	                            .questionOrder(
	                                    questionVO.getQuestionOrder()
	                            )
	                            .build();

	            questionDao.insert(questionDto);

	            //새로 생성된 번호
	            questionNo = newQuestionNo;

	            //응답 VO에도 반영
	            questionVO.setQuestionNo(
	                    newQuestionNo
	            );
	        }


	        //================================================
	        // 기존 문제 수정
	        //================================================
	        else {

	            QuestionDto before =
	                    questionDao.selectOne(questionNo);

	            if (before == null) {
	                throw new TargetNotfoundException();
	            }

	            //다른 시험 문제 수정 방지
	            if (before.getExamNo() != examNo) {
	                throw new GetOutException();
	            }


	            QuestionDto questionDto =
	                    QuestionDto.builder()
	                            .questionNo(questionNo)
	                            .questionContent(
	                                    questionVO.getQuestionContent()
	                            )
	                            .questionScore(
	                                    questionVO.getQuestionScore()
	                            )
	                            .questionComment(
	                                    questionVO.getQuestionComment()
	                            )
	                            .questionOrder(
	                                    questionVO.getQuestionOrder()
	                            )
	                            .build();

	            questionDao.update(questionDto);
	        }


	        //==================================================
	        // 6. 보기 저장
	        //==================================================

	        if (questionVO.getOptionList() == null) {
	            continue;
	        }


	        for (QuestionOptionDraftVO optionVO
	                : questionVO.getOptionList()) {

	            Integer optionNo =
	                    optionVO.getOptionNo();


	            //==============================================
	            // 신규 보기
	            //==============================================
	            if (optionNo == null) {

	                int newOptionNo =
	                        questionOptionDao.sequence();

	                QuestionOptionDto optionDto =
	                        QuestionOptionDto.builder()
	                                .optionNo(newOptionNo)
	                                .questionNo(questionNo)
	                                .optionContent(
	                                        optionVO.getOptionContent()
	                                )
	                                .optionIsAnswer(
	                                        optionVO.getOptionIsAnswer()
	                                )
	                                .optionOrder(
	                                        optionVO.getOptionOrder()
	                                )
	                                .build();

	                questionOptionDao.insert(optionDto);

	                //응답에 생성된 번호 반영
	                optionVO.setOptionNo(
	                        newOptionNo
	                );
	            }


	            //==============================================
	            // 기존 보기 수정
	            //==============================================
	            else {

	                QuestionOptionDto beforeOption =
	                        questionOptionDao.selectOne(
	                                optionNo
	                        );

	                if (beforeOption == null) {
	                    throw new TargetNotfoundException();
	                }

	                //이 문제의 보기가 맞는지
	                if (beforeOption.getQuestionNo() != questionNo) {
	                    throw new GetOutException();
	                }


	                QuestionOptionDto optionDto =
	                        QuestionOptionDto.builder()
	                                .optionNo(optionNo)
	                                .optionContent(
	                                        optionVO.getOptionContent()
	                                )
	                                .optionIsAnswer(
	                                        optionVO.getOptionIsAnswer()
	                                )
	                                .optionOrder(
	                                        optionVO.getOptionOrder()
	                                )
	                                .build();

	                questionOptionDao.update(optionDto);
	            }
	        }
	    }
	    //신규 생성된 questionNo / optionNo가 들어간 상태로 반환
	    return request;
	}

	@Override
	public ExamStatisticsVO selectStatistics(int examNo, int employeeNo, boolean tutor) {
		//강사는 본인 시험만 조회 가능
		checkAuthority(examNo, employeeNo, tutor);
		
		//시험 전체 통계
		ExamStatisticsVO statistics = examDao.selectStatistics(examNo);
		
		if(statistics == null) {
			throw new TargetNotfoundException();
		}
		
		//문항별 통계
		List<QuestionStatisticsVO> questionStatistics = examDao.selectQuestionStatistics(examNo);
		
		statistics.setQuestionStatistics(questionStatistics);
		
		return statistics;
	}

}
