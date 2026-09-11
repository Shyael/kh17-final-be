package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.vo.exam.ExamAttemptListVO;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.ExamSearchVO;
import com.kh.khedu.vo.exam.ExamStatisticsVO;
import com.kh.khedu.vo.exam.QuestionStatisticsVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;
import com.kh.khedu.vo.exam.StudentExamSearchVO;

public interface ExamDao {
    // 시험 번호 시퀀스 생성
    int sequence();
    
    // 시험 등록
    void insert(ExamDto examDto);
    
    // 시험 번호로 단일 시험 조회(DB 데이터 확인용)
    ExamDto selectOne(int examNo);
    
    // 시험 상세 조회(화면 출력용)
    ExamDetailVO selectDetail(int examNo);
    
    // 학생용 시험 상세 조회
    StudentExamDetailVO selectDetailByStudent(int examNo, int studentNo);
    
    // 특정 강의의 최근 시험 5개 조회
    List<ExamListVO> selectRecentListByCourse(int courseNo);
    
    // 학생 시험 목록 조회
    List<StudentExamListVO> selectListByStudent(int studentNo);
   
    //강사/관리자 시험 목록
    List<ExamListVO> selectManageSearchList(ExamSearchVO search);
    int selectManageCount(ExamSearchVO search);
    
    //학생 시험 목록
    List<StudentExamListVO> selectStudentSearchList(StudentExamSearchVO search);
    int selectStudentCount(StudentExamSearchVO search);
    
    // 시험 정보 수정
    boolean update(ExamDto examDto);
    
    // 시험 삭제
    boolean delete(int examNo);
    
    //강사용 시험 응시자 목록
    List<ExamAttemptListVO> selectAttemptList(int examNo);
    
    //제출 현황 및 시험 현황
    ExamStatisticsVO selectStatistics(int examNo);
    
    //문항별 통계
    List<QuestionStatisticsVO> selectQuestionStatistics(int examNo);
}