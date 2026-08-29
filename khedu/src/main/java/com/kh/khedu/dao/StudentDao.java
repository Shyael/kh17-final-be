package com.kh.khedu.dao;

import com.kh.khedu.student.StudentVO;

public interface StudentDao {

	int sequence(); //등록
	void insert(StudentVO studentVO);

}
