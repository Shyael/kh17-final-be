package com.kh.khedu.dao;

import com.kh.khedu.vo.student.StudentVO;

public interface StudentDao {

	int sequence(); //등록
	void insert(StudentVO studentVO);

}
