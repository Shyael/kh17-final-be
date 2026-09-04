package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;

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
    // 전체 시험 목록 조회
    List<ExamListVO> selectList();
    // 특정 강의의 시험 목록 조회
    List<ExamListVO> selectListByCourse(int courseNo);
    // 특정 강사가 등록한 시험 목록 조회
    List<ExamListVO> selectListByEmployee(int employeeNo);
    // 학생 시험 목록 조회
    List<StudentExamListVO> selectListByStudent(int studentNo);
    // 시험 정보 수정
    boolean update(ExamDto examDto);
    // 시험 삭제
    boolean delete(int examNo);
    
}