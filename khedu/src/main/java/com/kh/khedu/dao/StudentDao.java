package com.kh.khedu.dao;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.vo.parent.ParentStudentVO;
import com.kh.khedu.vo.student.StudentVO;
import com.kh.khedu.vo.studentLink.StudentLinkVO;

public interface StudentDao {

	int sequence(); //등록
	void insert(StudentVO studentVO);
	StudentDto selectOne(int accountNo);
	boolean updateAll(StudentDto studentDto);
	ParentStudentVO selectOneRelationByAccountNo(int accountNo);
	
	//student_link관련
	int sequenceLink(); //등록
	// 만료된 코드 업데이트
	void expireStudentLink(int studentNo);
	// DB에 저장
	void insertStudentLink(StudentLinkVO studentLinkVO);
}
