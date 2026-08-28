package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
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

    // 전체 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectList() {
        return sqlSession.selectList("mapper.assignment.selectList");
    }

    // 특정 강의의 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectListByCourse(int courseNo) {
        return sqlSession.selectList(
                "mapper.assignment.selectListByCourse", courseNo);
    }

    // 특정 강사가 등록한 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectListByEmployee(int employeeNo) {
        return sqlSession.selectList(
                "mapper.assignment.selectListByEmployee", employeeNo);
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

}