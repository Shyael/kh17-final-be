package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;

public interface AssignmentService {
    // 과제 등록
    int insert(AssignmentDto assignmentDto);

    // 과제 상세 조회
    AssignmentDetailVO selectOne(int assignmentNo);

    // 전체 과제 목록 조회
    List<AssignmentListVO> selectList();

    // 특정 강의의 과제 목록 조회
    List<AssignmentListVO> selectListByCourse(int courseNo);

    // 특정 강사가 등록한 과제 목록 조회
    List<AssignmentListVO> selectListByEmployee(int employeeNo);

    // 학생이 수강 중인 강의의 과제 목록 조회
    List<StudentAssignmentListVO> selectListByStudent(int studentNo);

    // 과제 수정
    boolean update(AssignmentDto assignmentDto);

    // 과제 삭제
    boolean delete(int assignmentNo);
}