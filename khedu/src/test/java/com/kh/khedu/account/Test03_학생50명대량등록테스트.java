package com.kh.khedu.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.khedu.service.StudentService; // 실제 joinStudent 메서드가 속한 서비스로 임포트
import com.kh.khedu.vo.account.AccountJoinResponseVO;
import com.kh.khedu.vo.student.StudentJoinRequestVO; // 실제 VO 패키지 경로에 맞게 확인

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test03_학생50명대량등록테스트 {

    @Autowired
    private StudentService studentService; // joinStudent가 있는 Service 주입

    @Test
    public void register50Students() {
        int totalCount = 50;

        String[] schools = {"역삼중학교", "강남고등학교", "대치중학교", "서초고등학교", "도곡중학교"};
        String[] grades = {"1학년", "2학년", "3학년"};
        String[] genders = {"남", "여"}; // DB 제약조건에 따라 'M'/'F' 또는 '남'/'여'로 조정 가능

        for (int i = 1; i <= totalCount; i++) {
            String formattedIndex = String.format("%03d", i);
            String accountId = "student" + formattedIndex + "@khedu.com";
            
            // 010 + 1~9로 시작하는 4자리 + 4자리 (정규식 '^010[1-9][0-9]{7}$' 준수)
            String accountPhone = "010" + (2000 + i) + String.format("%04d", i);
            String accountName = "학생" + formattedIndex;

            String school = schools[i % schools.length];
            String grade = grades[i % grades.length];
            String gender = genders[i % genders.length];

            StudentJoinRequestVO requestVO = StudentJoinRequestVO.builder()
                    .accountId(accountId)
                    .accountPassword("TestPass123!") // 비밀번호 정책 통과
                    .accountName(accountName)
                    .accountPhone(accountPhone)
                    .accountBirth("2008-05-15")     // YYYY-MM-DD 포맷
                    .studentSchool(school)
                    .studentGrade(grade)
                    .studentGender(gender)
                    .studentEtc("50명 대량 생성 테스트 계정")
                    .build();

            try {
                AccountJoinResponseVO response = studentService.joinStudent(requestVO);
                log.info("[{}/{}] 등록 완료 - ID: {}, 이름: {}, 번호: {}, 학생번호: {}", 
                        i, totalCount, response.getAccountId(), response.getAccountName(), accountPhone, response.getTargetNo());
            } catch (Exception e) {
                log.error("[{}/{}] 등록 실패: {} - 사유: {}", i, totalCount, accountId, e.getMessage(), e);
            }
        }
    }
}