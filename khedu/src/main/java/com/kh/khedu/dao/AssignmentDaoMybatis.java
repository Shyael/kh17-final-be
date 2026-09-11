package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.AssignmentSearchVO;
import com.kh.khedu.vo.assignment.AssignmentStudentSearchVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;

@Repository
public class AssignmentDaoMybatis implements AssignmentDao {

    @Autowired
    private SqlSession sqlSession;

    // 과제 번호 생성
    @Override
    public int sequence() {
    	return sqlSession.selectOne("mapper.assignment.sequence");
    }

    // 과제 등록
    @Override
    public void insert(AssignmentDto assignmentDto) {
        sqlSession.insert("mapper.assignment.insert", assignmentDto);
    }

    // 과제 상세 조회
    @Override
    public AssignmentDetailVO selectOne(int assignmentNo) {
        return sqlSession.selectOne(
                "mapper.assignment.selectOne", assignmentNo);
    }

    // 특정 강의의 최근 과제 5개
    @Override
	public List<AssignmentListVO> selectRecentListByCourse(int courseNo) {
    	return sqlSession.selectList("mapper.assignment.selectRecentListByCourse", courseNo);
	}

    // 학생이 수강 중인 강의의 과제 목록 조회
    @Override
    public List<StudentAssignmentListVO> selectListByStudent(int studentNo) {
        return sqlSession.selectList(
                "mapper.assignment.selectListByStudent", studentNo);
    }

    // 과제 수정
    @Override
    public boolean update(AssignmentDto assignmentDto) {
        return sqlSession.update(
                "mapper.assignment.update", assignmentDto) > 0;
    }

    // 과제 삭제
    @Override
    public boolean delete(int assignmentNo) {
        return sqlSession.delete(
                "mapper.assignment.delete", assignmentNo) > 0;
    }

	@Override
	public void connect(int assignmentNo, int attachNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("assignmentNo", assignmentNo);
		params.put("attachNo", attachNo);
		sqlSession.insert("mapper.assignment.connect", params);
		
	}

	@Override
	public List<Integer> selectFiles(int assignmentNo) {
		return sqlSession.selectList("mapper.assignment.selectFiles",assignmentNo);
	}

	@Override
	public List<AssignmentListVO> selectManageSearchList(AssignmentSearchVO search) {
		return sqlSession.selectList("mapper.assignment.selectManageSearchList", search);
	}

	@Override
	public int selectManageCount(AssignmentSearchVO search) {
		return sqlSession.selectOne("mapper.assignment.selectManageCount", search);
	}

	@Override
	public List<StudentAssignmentListVO> selectStudentSearchList(AssignmentStudentSearchVO search) {
		return sqlSession.selectList("mapper.assignment.selectStudentSearchList" ,search);
	}

	@Override
	public int selectStudentCount(AssignmentStudentSearchVO search) {
		return sqlSession.selectOne("mapper.assignment.selectStudentCount", search);
	}
	
	@Override
	public boolean close(int assignmentNo) {
	    return sqlSession.update("mapper.assignment.close", assignmentNo) > 0;
	}

	

}