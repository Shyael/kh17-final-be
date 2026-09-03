package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ExamDto;

@Repository
public class ExamDaoMybatis implements ExamDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	//시험 번호 시퀀스 생성
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.exam.sequence");
	}

	//시험 등록
	@Override
	public void insert(ExamDto examDto) {
		sqlSession.insert("mapper.exam.insert", examDto);
	}

	//시험 번호로 단일 시험 조회
	@Override
	public ExamDto selectOne(int examNo) {
		return sqlSession.selectOne("mapper.exam.selectOne", examNo); 
	}

	//전체 시험 목록 조회
	@Override
	public List<ExamDto> selectList() {
		return sqlSession.selectList("mapper.exam.selectList");
	}
	
	//특정 강의의 시험 목록 조회
	@Override
	public List<ExamDto> selectListByCourse(int courseNo) {
		return sqlSession.selectList("mapper.exam.selectListByCourse", courseNo);
	}

	//특정 강사가 등록한 시험 목록 조회
	@Override
	public List<ExamDto> selectListByEmployee(int employeeNo) {
		return sqlSession.selectList("mapper.exam.selectListByEmployee", employeeNo);
	}

	//시험 정보 수정
	//수정된 행이 1개 이상이면 true 반환
	@Override
	public boolean update(ExamDto examDto) {
		return sqlSession.update("mapper.exam.update", examDto) > 0;
	}

	//시험 삭제
	// 삭제된 행이 1개 이상이면 true 반환
	@Override
	public boolean delete(int examNo) {
		return sqlSession.delete("mapper.exam.delete", examNo) > 0;
	}

}
