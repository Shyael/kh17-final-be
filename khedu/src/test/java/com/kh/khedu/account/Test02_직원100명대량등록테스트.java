package com.kh.khedu.account;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.vo.account.AccountRegisterVO;
import com.kh.khedu.vo.employee.EmployeeVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class Test02_직원100명대량등록테스트 {

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void register100Employees() {
        int totalCount = 100;
        String rawPassword = "Testuser123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        for (int i = 1; i <= totalCount; i++) {
            // 데스크 50명, 강사 50명 번갈아 등록
            String employeeType = (i % 2 == 0) ? "데스크" : "강사";

            String formattedIndex = String.format("%03d", i);
            String accountId = "emp" + formattedIndex + "@khedu.com";
            
            // CHECK (regexp_like(account_phone, '^010[1-9][0-9]{7}$')) 준수
            // 010 다음에 '1'부터 시작하도록 1000 + i (예: 01010010001, 01010020002 ...)
            String accountPhone = "010" + (1000 + i) + String.format("%04d", i);
            String accountName = employeeType + formattedIndex;

            try {
                // ==========================================
                // [1] ACCOUNT 등록
                // ==========================================
                int accountNo = accountDao.sequence();

                AccountRegisterVO accountVO = AccountRegisterVO.builder()
                        .accountNo(accountNo)
                        .accountId(accountId)
                        .accountPassword(encodedPassword)
                        .accountName(accountName)
                        .accountPhone(accountPhone)
                        .accountBirth("1995-01-01") // YYYY-MM-DD 정규식 통과
                        .accountStatus("N")         // 'Y' 또는 'N' 통과 (기본값 'N')
                        .accountType("직원")         // ACCOUNT_TYPE_CK ('학생', '학부모', '직원') 통과
                        .build();

                sqlSession.insert("mapper.account.register", accountVO);

                // ==========================================
                // [2] EMPLOYEE 등록
                // ==========================================
                int employeeNo = employeeDao.sequence();

                EmployeeVO employeeVO = EmployeeVO.builder()
                        .employeeNo(employeeNo)
                        .accountNo(accountNo)
                        .employeeType(employeeType)
                        .employeeHtime(Timestamp.valueOf(LocalDate.of(2026, 8, 25).atStartOfDay()))
                        .build();

                sqlSession.insert("mapper.employee.register", employeeVO);

                log.info("[{}/{}] 등록 성공: {} | 구분: {} | 전화번호: {} | AccountNo: {}", 
                        i, totalCount, accountId, employeeType, accountPhone, accountNo);

            } catch (Exception e) {
                log.error("[{}/{}] 등록 실패: {} - 사유: {}", i, totalCount, accountId, e.getMessage());
            }
        }
    }
}