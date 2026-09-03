package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AcademyDto;

public interface AcademyDao {
	int sequence(); // 등록
	void insert(AcademyDto academyDto);
	AcademyDto selectOne();
	boolean update(AcademyDto academyDto);
	boolean delete(int academyNo);
	
	//파일
	void connect(int academyNo, int attachNo);
	List<Integer> selectDetailImages(int academyNo);
}
