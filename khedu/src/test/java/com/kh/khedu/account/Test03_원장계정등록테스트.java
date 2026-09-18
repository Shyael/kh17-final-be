package com.kh.khedu.account;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
public class Test03_원장계정등록테스트 {

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void register2Directors() {
        String[][] directors = {
            {"khedu1@naver.com", "Khedu1!", "원장1", "01077770001"},
            {"khedu2@naver.com", "Khedu2!", "원장2", "01077770002"}
        };

        final int ADMIN_ROLE_NO = 5; // 원장 (ADMIN)

        for (int i = 0; i < directors.length; i++) {
            String accountId = directors[i][0];
            String rawPassword = directors[i][1];
            String accountName = directors[i][2];
            String accountPhone = directors[i][3];

            String encodedPassword = passwordEncoder.encode(rawPassword);

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
                        .accountBirth("1980-01-01")
                        .accountStatus("N")
                        .accountType("직원")
                        .build();

                sqlSession.insert("mapper.account.register", accountVO);

                // ==========================================
                // [2] EMPLOYEE 등록
                // ==========================================
                int employeeNo = employeeDao.sequence();

                EmployeeVO employeeVO = EmployeeVO.builder()
                        .employeeNo(employeeNo)
                        .accountNo(accountNo)
                        .employeeType("원장")
                        .employeeHtime(Timestamp.valueOf(LocalDate.now().atStartOfDay()))
                        .build();

                sqlSession.insert("mapper.employee.register", employeeVO);

                // ==========================================
                // [3] ACCOUNT_ROLES 권한 등록 (ROLE_NO: 5 / ADMIN)
                // ==========================================
                Map<String, Object> roleParams = new HashMap<>();
                roleParams.put("accountNo", accountNo);
                roleParams.put("roleNo", ADMIN_ROLE_NO);

                // 기존 프로젝트에 정의된 매퍼 ID 사용 (예: mapper.account.insertRole 또는 mapper.accountRole.insert)
                // 만약 매퍼가 없다면 아래 쿼리를 매퍼 XML에 등록하거나 해당 네임스페이스를 맞춰주세요.
                sqlSession.insert("mapper.account.insertRole", roleParams);

                log.info("[등록 성공] AccountNo: {} | EmployeeNo: {} | ID: {} | RoleNo: {} (ADMIN)", 
                        accountNo, employeeNo, accountId, ADMIN_ROLE_NO);

            } catch (Exception e) {
                log.error("[등록 실패] ID: {} - 사유: {}", accountId, e.getMessage(), e);
            }
        }
    }
}