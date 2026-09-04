package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AcademyHistoryDto;

public interface AcademyHistoryDao {
	int sequence(); // 시퀀스번호
	void insert(AcademyHistoryDto academyHistoryDto);
	List<AcademyHistoryDto> selectList(int academyNo);
	AcademyHistoryDto selectOne(int academyHistoryNo);
	boolean update(AcademyHistoryDto academyHistoryDto);
	boolean delete(int academyHistoryNo);
}
