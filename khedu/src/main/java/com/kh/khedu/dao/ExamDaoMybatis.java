package com.kh.khedu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ExamDto;
import com.kh.khedu.vo.exam.ExamAttemptListVO;
import com.kh.khedu.vo.exam.ExamDetailVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.exam.ExamSearchVO;
import com.kh.khedu.vo.exam.ExamStatisticsVO;
import com.kh.khedu.vo.exam.QuestionStatisticsVO;
import com.kh.khedu.vo.exam.StudentExamDetailVO;
import com.kh.khedu.vo.exam.StudentExamListVO;
import com.kh.khedu.vo.exam.StudentExamSearchVO;

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

	// 특정 강의의 최근 시험 5개 조회
	@Override
	public List<ExamListVO> selectRecentListByCourse(int courseNo) {
	    return sqlSession.selectList("mapper.exam.selectRecentListByCourse", courseNo);
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

	//강사용 수강생 제출목록
	@Override
	public List<ExamAttemptListVO> selectAttemptList(int examNo) {
		return sqlSession.selectList("mapper.exam.selectAttemptList", examNo);
	}

	@Override
	public ExamStatisticsVO selectStatistics(int examNo) {
		return sqlSession.selectOne("mapper.exam.selectStatistics", examNo);
	}

	@Override
	public List<QuestionStatisticsVO> selectQuestionStatistics(int examNo) {
		return sqlSession.selectList("mapper.exam.selectQuestionStatistics", examNo);
	}

	@Override
	public List<ExamListVO> selectManageSearchList(ExamSearchVO search) {
		return sqlSession.selectList("mapper.exam.selectManageSearchList", search);
	}

	@Override
	public int selectManageCount(ExamSearchVO search) {
		return sqlSession.selectOne("mapper.exam.selectManageCount", search);
	}

	@Override
	public List<StudentExamListVO> selectStudentSearchList(StudentExamSearchVO search) {
		return sqlSession.selectList("mapper.exam.selectStudentSearchList", search);
	}

	@Override
	public int selectStudentCount(StudentExamSearchVO search) {
		return sqlSession.selectOne("mapper.exam.selectStudentCount", search);
	}
}
