package com.kh.khedu.service.payroll;

import org.springframework.stereotype.Service;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public interface ContractAuthorizationService {
	boolean checkAdmin(TokenParseResponseVO parseVO);
	boolean checkPartyB(TokenParseResponseVO parseVO,long contractNo);
	boolean checkAdminOrPartyB(TokenParseResponseVO parseVO,long contractNo);
	boolean checkAdminOrPartyBOrDeskByContract(TokenParseResponseVO parseVO, long contractNo);
	   // 직원번호를 기준으로 권한 검사
    boolean checkAdminOrPartyBOrDeskByEmployee(
            TokenParseResponseVO parseVO,
            int employeeNo
    );


}
