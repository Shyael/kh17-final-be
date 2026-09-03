package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dto.ParentStudentDto;
import com.kh.khedu.vo.parentStudent.StudentParentDetailVO;

@Service
public class ParentStudentService {

    @Autowired
    private ParentStudentDao parentStudentDao;

    // 🌟 원장님 다이렉트 연동 로직
    public void directLink(ParentStudentDto dto) {
        // 1. 이미 연동되어 있는지 중복 검사
        ParentStudentDto already = parentStudentDao.findByParentStudentNo(dto);
        if (already != null) {
            throw new RuntimeException("이미 연동된 학부모입니다."); // 프론트로 에러 메시지 전송
        }
        
        // 2. 중복이 아니면 즉시 INSERT
        parentStudentDao.insert(dto);
    }
    
    public List<StudentParentDetailVO> selectParentListByStudentNo(int studentNo) {
        return parentStudentDao.selectParentListByStudentNo(studentNo);
    }
    
 // 연동 해제 로직
    public void removeLink(ParentStudentDto dto) {
        boolean result = parentStudentDao.deleteRelationship(dto);
        if (!result) {
            throw new RuntimeException("해제할 연동 정보가 없거나 이미 삭제되었습니다.");
        }
    }
}