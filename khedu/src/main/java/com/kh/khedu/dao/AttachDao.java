package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AttachDto;

public interface AttachDao {
	int sequence(); //등록
	void insert(AttachDto attachDto);
	AttachDto selectOne(int attachNo);
	AttachDto selectOne(Integer attachNo);
	
	boolean delete(int attachNo);
	List<AttachDto> selectList(List<Integer> attachNumbers);
}
