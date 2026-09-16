package com.kh.khedu.error;

import org.springframework.stereotype.Component;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;
@Component
public class TutorChecker {
	public boolean TutorCheck(TokenParseResponseVO parseVO) {
		if(parseVO == null || parseVO.getRoleNames() == null ) 
			throw new GetOutException();
		if(parseVO.getRoleNames().contains("TUTOR"))
				throw new YouAreTutorException();
		return true;
		
	}
}
