package com.kh.khedu.dao;

import com.kh.khedu.dto.AcademyDto;

public interface AcademyDao {
	int sequence(); // 등록
	void insert(AcademyDto academyDto);
	AcademyDto selectOne();
	boolean update(AcademyDto academyDto);
	boolean delete(int academyNo);
//	abc
}
