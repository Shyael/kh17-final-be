package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;

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

	//시험 번호로 단일 시험 조회(DB조회용)
	@Override
	public ExamDto selectOne(int examNo) {
		return sqlSession.selectOne("mapper.exam.selectOne", examNo); 
	}
	
	//시험 상세 조회(화면용)
	@Override
	public ExamDetailVO selectDetail(int examNo) {
		return sqlSession.selectOne("mapper.exam.selectDetail", examNo);
	}
	
	@Override
	public StudentExamDetailVO selectDetailByStudent(int examNo, int studentNo) {
		Map<String, Object> params = Map.of(
				"examNo", examNo,
				"studentNo", studentNo
		);
		return sqlSession.selectOne("mapper.exam.selectDetailByStudent",params);
	}

	//전체 시험 목록 조회
	@Override
	public List<ExamListVO> selectList() {
		return sqlSession.selectList("mapper.exam.selectList");
	}
	
	//특정 강의의 시험 목록 조회
	@Override
	public List<ExamListVO> selectListByCourse(int courseNo) {
		return sqlSession.selectList("mapper.exam.selectListByCourse", courseNo);
	}

	//특정 강사가 등록한 시험 목록 조회
	@Override
	public List<ExamListVO> selectListByEmployee(int employeeNo) {
		return sqlSession.selectList("mapper.exam.selectListByEmployee", employeeNo);
	}
	
	@Override
	public List<StudentExamListVO> selectListByStudent(int studentNo) {
		return sqlSession.selectList("mapper.exam.selectListByStudent", studentNo);
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
