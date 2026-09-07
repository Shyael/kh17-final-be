package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;

public interface ExamService {
    // 시험 등록
    int insert(ExamDto examDto);
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
    // 전체 시험 목록 조회
    List<ExamListVO> selectList();
    // 특정 강의의 시험 목록 조회
    List<ExamListVO> selectListByCourse(int courseNo);
    // 특정 강사가 등록한 시험 목록 조회
    List<ExamListVO> selectListByEmployee(int employeeNo);
    //학생 시험 목록 조회
    List<StudentExamListVO> selectListByStudent(int studentNo);
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
}