package com.kh.khedu.service.payroll;

import org.springframework.stereotype.Service;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public interface ContractAuthorizationService {
	boolean checkAdmin(TokenParseResponseVO parseVO);
	boolean checkPartyB(TokenParseResponseVO parseVO,long contractNo);
	boolean checkAdminOrPartyB(TokenParseResponseVO parseVO,long contractNo);
	boolean checkAdminOrPartyBOrDesk(TokenParseResponseVO parseVO, long contractNo);
}
