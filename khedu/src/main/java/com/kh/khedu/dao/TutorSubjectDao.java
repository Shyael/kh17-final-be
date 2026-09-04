package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.TutorSubjectDto;

public interface TutorSubjectDao {

	int sequence();
	void insert(TutorSubjectDto tutorSubjectDto);
	List<TutorSubjectDto> selectList(int tutorNo);
	TutorSubjectDto selectOne(int tutorSubjectNo);
	boolean update(TutorSubjectDto tutorSubjectDto);
	boolean delete(int tutorSubjectNo);

}