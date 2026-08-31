package com.kh.khedu.dao;

import com.kh.khedu.dto.ParentDto;

public interface ParentDao {

	int sequence();
	void insert(ParentDto parentDto);

}
