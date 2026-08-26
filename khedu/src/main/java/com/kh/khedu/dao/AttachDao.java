package com.kh.khedu.dao;

import com.kh.khedu.dto.AttachDto;

public interface AttachDao {
	int sequence(); //등록
	void insert(AttachDto attachDto);
	AttachDto selectOne(int attachNo);
	boolean delete(int attachNo);
}
