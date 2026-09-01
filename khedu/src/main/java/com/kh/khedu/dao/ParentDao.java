package com.kh.khedu.dao;

import com.kh.khedu.dto.ParentDto;
import com.kh.khedu.vo.parent.ParentStudentVO;

public interface ParentDao {

	int sequence();
	void insert(ParentDto parentDto);
	ParentStudentVO selectOneRelationByAccountNo(int accountNo);
	ParentDto selectOneByAccountNo(int accountNo);

}
