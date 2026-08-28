package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AssignmentDao;
import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentDao assignmentDao;

    // 과제 등록
    @Override
    public int insert(AssignmentDto assignmentDto) {
    	//시퀀스번호 생성
        int assignmentNo = assignmentDao.sequence();
        
        assignmentDto.setAssignmentNo(assignmentNo);

        assignmentDao.insert(assignmentDto);

        return assignmentNo;
    }

    // 과제 상세 조회
    @Override
    public AssignmentDetailVO selectOne(int assignmentNo) {
        return assignmentDao.selectOne(assignmentNo);
    }

    // 전체 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectList() {
        return assignmentDao.selectList();
    }

    // 특정 강의의 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectListByCourse(int courseNo) {
        return assignmentDao.selectListByCourse(courseNo);
    }

    // 특정 강사가 등록한 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectListByEmployee(int employeeNo) {
        return assignmentDao.selectListByEmployee(employeeNo);
    }

    // 학생이 수강 중인 강의의 과제 목록 조회
    @Override
    public List<StudentAssignmentListVO> selectListByStudent(int studentNo) {
        return assignmentDao.selectListByStudent(studentNo);
    }

    // 과제 수정
    @Override
    public boolean update(AssignmentDto assignmentDto) {
        return assignmentDao.update(assignmentDto);
    }

    // 과제 삭제
    @Override
    public boolean delete(int assignmentNo) {
        return assignmentDao.delete(assignmentNo);
    }

}