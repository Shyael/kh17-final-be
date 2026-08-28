package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AssignmentSubmitDao;
import com.kh.khedu.dto.AssignmentSubmitDto;
import com.kh.khedu.vo.assignment.AssignmentSubmitDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitListVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitStudentListVO;

@Service
@Transactional
public class AssignmentSubmitServiceImpl implements AssignmentSubmitService {

    @Autowired
    private AssignmentSubmitDao assignmentSubmitDao;

    // 과제 제출 등록
    @Override
    public int insert(AssignmentSubmitDto assignmentSubmitDto) {
    	//시퀀스 번호 생성
        int submitNo = assignmentSubmitDao.sequence();
        
        assignmentSubmitDto.setSubmitNo(submitNo);
        assignmentSubmitDao.insert(assignmentSubmitDto);

        return submitNo;
    }

    // 특정 제출 상세 조회
    @Override
    public AssignmentSubmitDetailVO selectOne(int submitNo) {
        return assignmentSubmitDao.selectOne(submitNo);
    }

    // 특정 과제에 대한 특정 학생의 제출 조회
    @Override
    public AssignmentSubmitDetailVO selectOneByAssignmentStudent(
            AssignmentSubmitDto assignmentSubmitDto) {

        return assignmentSubmitDao.selectOneByAssignmentStudent(
                assignmentSubmitDto);
    }

    // 전체 과제 제출 목록 조회
    @Override
    public List<AssignmentSubmitListVO> selectList() {
        return assignmentSubmitDao.selectList();
    }

    // 특정 과제의 제출한 학생 목록 조회
    @Override
    public List<AssignmentSubmitListVO> selectListByAssignment(
            int assignmentNo) {

        return assignmentSubmitDao.selectListByAssignment(assignmentNo);
    }

    // 특정 과제의 전체 수강생 제출 현황 조회
    @Override
    public List<AssignmentSubmitStudentListVO> selectStudentListByAssignment(
            int assignmentNo) {

        return assignmentSubmitDao.selectStudentListByAssignment(
                assignmentNo);
    }

    // 제출 내용 수정
    @Override
    public boolean update(AssignmentSubmitDto assignmentSubmitDto) {
        return assignmentSubmitDao.update(assignmentSubmitDto);
    }

    // 강사 피드백 등록 및 수정
    @Override
    public boolean updateComment(AssignmentSubmitDto assignmentSubmitDto) {
        return assignmentSubmitDao.updateComment(assignmentSubmitDto);
    }

    // 과제 제출 삭제
    @Override
    public boolean delete(int submitNo) {
        return assignmentSubmitDao.delete(submitNo);
    }
}