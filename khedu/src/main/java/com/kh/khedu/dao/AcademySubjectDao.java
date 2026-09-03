package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.AcademySubjectDto;

public interface AcademySubjectDao {
	int sequence();
	void insert(AcademySubjectDto academySubjectDto);
	List<AcademySubjectDto> selectList(int academyNo);
	AcademySubjectDto selectOne(int academySubjectNo);
	boolean update(AcademySubjectDto academySubjectDto);
	boolean delete(int academySubjectNo);
}
