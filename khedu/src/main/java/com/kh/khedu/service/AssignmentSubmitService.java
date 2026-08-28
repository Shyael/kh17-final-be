package com.kh.khedu.service;

import java.util.List;

import com.kh.khedu.dto.AssignmentSubmitDto;
import com.kh.khedu.vo.assignment.AssignmentSubmitDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitListVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitStudentListVO;

public interface AssignmentSubmitService {

    // 과제 제출 등록
    int insert(AssignmentSubmitDto assignmentSubmitDto);

    // 특정 제출 상세 조회
    AssignmentSubmitDetailVO selectOne(int submitNo);

    // 특정 과제에 대한 특정 학생의 제출 조회
    AssignmentSubmitDetailVO selectOneByAssignmentStudent(
            AssignmentSubmitDto assignmentSubmitDto);

    // 전체 과제 제출 목록 조회
    List<AssignmentSubmitListVO> selectList();

    // 특정 과제의 제출한 학생 목록 조회
    List<AssignmentSubmitListVO> selectListByAssignment(int assignmentNo);

    // 특정 과제의 전체 수강생 제출 현황 조회
    List<AssignmentSubmitStudentListVO> selectStudentListByAssignment(
            int assignmentNo);

    // 제출 내용 수정
    boolean update(AssignmentSubmitDto assignmentSubmitDto);

    // 강사 피드백 등록 및 수정
    boolean updateComment(AssignmentSubmitDto assignmentSubmitDto);

    // 과제 제출 삭제
    boolean delete(int submitNo);

}