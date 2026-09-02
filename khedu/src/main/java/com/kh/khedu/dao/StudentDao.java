package com.kh.khedu.dao;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;
import com.kh.khedu.vo.student.StudentVO;
import com.kh.khedu.vo.studentLink.StudentLinkVO;

public interface StudentDao {

	int sequence(); //등록
	void insert(StudentVO studentVO);
	StudentDto selectOne(int accountNo);
	boolean updateAll(StudentDto studentDto);
	ParentStudentVO selectOneRelationByAccountNo(int accountNo);
}
