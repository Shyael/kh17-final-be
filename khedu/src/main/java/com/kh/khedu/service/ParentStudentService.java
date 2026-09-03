package com.kh.khedu.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🌟 필수!

import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dao.StudentLinkDao; // 팀원분 DAO
import com.kh.khedu.dto.ParentStudentDto;
import com.kh.khedu.vo.studentLink.StudentLinkVO;

@Service
public class ParentStudentService {

    @Autowired
    private ParentStudentDao parentStudentDao;
    
    @Autowired
    private StudentLinkDao studentLinkDao;
    
    @Autowired
    private RandomService randomService; // 팀원분이 만든 난수 생성기

    // 🌟 원장님 "딸깍" -> 백엔드 "슈루룩" 마법의 로직
    @Transactional // 하나라도 실패하면 전부 취소(롤백)되도록 보장
    public void directLinkWithToken(ParentStudentDto dto) {
        
        // [검증] 이미 연동되어 있는지 확인
        ParentStudentDto already = parentStudentDao.findByParentStudentNo(dto);
        if (already != null) {
            throw new RuntimeException("이미 연동된 학부모입니다."); 
        }
        
        // 1. 랜덤 코드 생성
        String linkCode = randomService.generateLinkCode();
        Timestamp expire = Timestamp.valueOf(LocalDateTime.now().plusHours(1));
        int studentLinkNo = studentLinkDao.sequenceLink();
        
        StudentLinkVO studentLinkVO = StudentLinkVO.builder()
                    .studentLinkNo(studentLinkNo)
                    .studentNo(dto.getStudentNo())
                    .linkCode(linkCode)
                    .linkExpire(expire)
                .build(); 
                
        // 2. student_link 테이블에 인서트 (토큰 생성)
        studentLinkDao.insertStudentLink(studentLinkVO);
        
        // 3. parent_student 테이블에 진짜 부모-자식 관계 묶기 (INSERT)
        parentStudentDao.insert(dto);
        
        // 4. 방금 만든 토큰을 즉시 "사용됨(Y)"으로 처리 (UPDATE)
        // 팀원이 만들어둔 usedLinkCode를 그대로 씁니다.
        studentLinkDao.usedLinkCode(studentLinkNo);
    }
}