package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AssignmentSubmitDto;
import com.kh.khedu.vo.assignment.AssignmentSubmitDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitListVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitStudentListVO;

@Repository
public class AssignmentSubmitDaoMybatis implements AssignmentSubmitDao {

    @Autowired
    private SqlSession sqlSession;

    // 과제 제출 번호 생성
    @Override
    public int sequence() {
        return sqlSession.selectOne(
                "mapper.assignmentSubmit.sequence");
    }

    // 과제 제출 등록
    @Override
    public void insert(AssignmentSubmitDto assignmentSubmitDto) {
        sqlSession.insert(
                "mapper.assignmentSubmit.insert",
                assignmentSubmitDto);
    }

    // 특정 제출 상세 조회
    @Override
    public AssignmentSubmitDetailVO selectOne(int submitNo) {
        return sqlSession.selectOne(
                "mapper.assignmentSubmit.selectOne",
                submitNo);
    }

    // 특정 과제에 대한 특정 학생의 제출 조회
    @Override
    public AssignmentSubmitDetailVO selectOneByAssignmentStudent(
            AssignmentSubmitDto assignmentSubmitDto) {

        return sqlSession.selectOne(
                "mapper.assignmentSubmit.selectOneByAssignmentStudent",
                assignmentSubmitDto);
    }

    // 전체 과제 제출 목록 조회
    @Override
    public List<AssignmentSubmitListVO> selectList() {
        return sqlSession.selectList(
                "mapper.assignmentSubmit.selectList");
    }

    // 특정 과제의 제출한 학생 목록 조회
    @Override
    public List<AssignmentSubmitListVO> selectListByAssignment(
            int assignmentNo) {

        return sqlSession.selectList(
                "mapper.assignmentSubmit.selectListByAssignment",
                assignmentNo);
    }

    // 특정 과제의 전체 수강생 제출 현황 조회
    @Override
    public List<AssignmentSubmitStudentListVO> selectStudentListByAssignment(
            int assignmentNo) {

        return sqlSession.selectList(
                "mapper.assignmentSubmit.selectStudentListByAssignment",
                assignmentNo);
    }

    // 제출 내용 수정
    @Override
    public boolean update(AssignmentSubmitDto assignmentSubmitDto) {
        return sqlSession.update(
                "mapper.assignmentSubmit.update",
                assignmentSubmitDto) > 0;
    }

    // 강사 피드백 등록 및 수정
    @Override
    public boolean updateComment(AssignmentSubmitDto assignmentSubmitDto) {
        return sqlSession.update(
                "mapper.assignmentSubmit.updateComment",
                assignmentSubmitDto) > 0;
    }

    // 과제 제출 삭제
    @Override
    public boolean delete(int submitNo) {
        return sqlSession.delete(
                "mapper.assignmentSubmit.delete",
                submitNo) > 0;
    }

	@Override
	public void connect(int submitNo, int attachNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("submitNo", submitNo);
		params.put("attachNo", attachNo);
		sqlSession.insert("mapper.assignmentSubmit.connect", params);
		
	}

	@Override
	public List<Integer> selectFiles(int submitNo) {
		return sqlSession.selectList("mapper.assignmentSubmit.selectFiles",submitNo);
	}

	@Override
	public List<Integer> selectFilesByAssignment(int assignmentNo) {
		return sqlSession.selectList("mapper.assignmentSubmit.selectFilesByAssignment",assignmentNo);
	}

}