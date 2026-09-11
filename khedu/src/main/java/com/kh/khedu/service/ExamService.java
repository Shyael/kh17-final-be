package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.exam.ExamAttemptListVO;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamDraftRequestVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.ExamSearchVO;
import com.kh.khedu.vo.exam.ExamStatisticsVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;
import com.kh.khedu.vo.exam.StudentExamSearchVO;

public interface ExamService {
    // 시험 등록
	int insert(
	        ExamDto examDto,
	        int loginEmployeeNo,
	        boolean tutor
	);
    // 시험 단일 조회(DB 조회용)
    ExamDto selectOne(int examNo);
    // 시험 상세 조회(화면용)
    ExamDetailVO selectDetail(
            int examNo,
            int employeeNo,
            boolean tutor
    );
    // 학생용 상세조회
    StudentExamDetailVO selectDetailByStudent(int examNo, int studentNo);
    
    // 특정 강의의 최근 시험 5개 조회
    List<ExamListVO> selectRecentListByCourse(int courseNo);
    
    //학생 시험 목록 조회
    List<StudentExamListVO> selectListByStudent(int studentNo);
    //강사 페이지네이션 + 검색 + 시험 목록
    PageResponseVO<ExamListVO> selectManageList(
            ExamSearchVO search,
            int employeeNo,
            boolean tutor
    );
    //학생/학부모 페이지네이션 + 검색 + 시험 목록
    PageResponseVO<StudentExamListVO> selectStudentList(
            StudentExamSearchVO search,
            int studentNo
    );
    
    // 시험 수정
    boolean update(
            ExamDto examDto,
            int employeeNo,
            boolean tutor
    );
    // 시험 삭제
    boolean delete(
            int examNo,
            int employeeNo,
            boolean tutor
    );
    //강사용 응시자 목록 조회
    List<ExamAttemptListVO> selectAttemptList(
            int examNo,
            int employeeNo,
            boolean tutor
    );
    //일괄저장
    ExamDraftRequestVO saveDraft(
	    int examNo,
	    ExamDraftRequestVO request,
	    int employeeNo,
	    boolean tutor
	);
    //통계 VO
    ExamStatisticsVO selectStatistics(
    		int examNo,
    		int employeeNo,
    		boolean tutor
    );
    
}