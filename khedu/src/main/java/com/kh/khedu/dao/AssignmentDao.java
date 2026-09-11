package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.AssignmentSearchVO;
import com.kh.khedu.vo.assignment.AssignmentStudentSearchVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;

public interface AssignmentDao {
	// 과제 번호 생성
	int sequence();
	
	// 과제 등록
	void insert(AssignmentDto assignmentDto);
	
	// 과제 상세 조회
	AssignmentDetailVO selectOne(int assignmentNo);


	// 강의 상세에서 보여줄 강의에 포함된 과제( 갯수 5개 )
	List<AssignmentListVO> selectRecentListByCourse(int courseNo);

	// 학생이 수강 중인 강의의 과제 목록 조회
	List<StudentAssignmentListVO> selectListByStudent(int studentNo);
	
	//강사용 과제 목록 + 검색 + 페이지네이션
	List<AssignmentListVO> selectManageSearchList(AssignmentSearchVO search);
	
	//강사용 검색 결과 전체 개수
	int selectManageCount(AssignmentSearchVO search);
	
	//학생용 과제 목록 + 페이지네이션
	List<StudentAssignmentListVO> selectStudentSearchList(
			AssignmentStudentSearchVO search
	);
	
	//학생용 검색 결과 전체 개수
	int selectStudentCount(
			AssignmentStudentSearchVO search
	);

	// 과제 수정
	boolean update(AssignmentDto assignmentDto);

	// 과제 삭제
	boolean delete(int assignmentNo);
	
	//파일
	void connect(int assignmentNo, int attachNo);
	List<Integer> selectFiles(int assignmentNo);
}
