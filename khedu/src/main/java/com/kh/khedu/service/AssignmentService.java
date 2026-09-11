package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.AssignmentSearchVO;
import com.kh.khedu.vo.assignment.AssignmentStudentSearchVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;

public interface AssignmentService {
    // 과제 등록
	int insert(
	        AssignmentDto assignmentDto,
	        List<MultipartFile> files,
	        int loginEmployeeNo,
	        boolean tutor
	) throws IllegalStateException, IOException;

    // 과제 상세 조회
    AssignmentDetailVO selectOne(int assignmentNo);


    // 특정 강의의 최근 5가지 과제 조회
    List<AssignmentListVO> selectRecentListByCourse(int courseNo);
    
    // 학생이 수강 중인 강의의 과제 목록 조회
    List<StudentAssignmentListVO> selectListByStudent(int studentNo);
    
    // 강사 페이지네이션 + 검색 + 과제 목록
    PageResponseVO<AssignmentListVO> selectManageList(
    		AssignmentSearchVO search,
    		int employeeNo,
    		boolean tutor
    );
    
    // 학생/학부모 페이지네이셔녀 + 검색 + 과제목록
    PageResponseVO<StudentAssignmentListVO> selectStudentList(
            AssignmentStudentSearchVO search,
            int studentNo
    );

    // 과제 수정
    boolean update(
    	AssignmentDto assignmentDto,
    	List<MultipartFile> files,
    	int employee, 
    	boolean tutor
    ) throws IllegalStateException, IOException;

    // 과제 삭제
    boolean delete(int assignmentNoint,int employee, boolean tutor);
    
    //파일 삭제
    void deleteFile(int assignmentNo, int attachNo, int employee, boolean tutor);
    
    //학부모용 : 자녀 과제 목록 조회
    PageResponseVO<StudentAssignmentListVO> selectListByParentStudent(
            AssignmentStudentSearchVO search,
            int parentNo,
            int studentNo
    );
    // 학부모용 : 자녀 과제 상세 조회
    AssignmentDetailVO selectOneByParentStudent(int parentNo, int studentNo, int assignmentNo);
}