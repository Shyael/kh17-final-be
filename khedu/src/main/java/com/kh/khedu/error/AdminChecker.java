package com.kh.khedu.error;



import org.springframework.stereotype.Component;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;
@Component
public class AdminChecker {

	public boolean AdminCheck(TokenParseResponseVO parseVO) {


	    if (parseVO == null
	            || parseVO.getRoleNames() == null) {

	        return false;
	    }

	    return parseVO.getRoleNames()
	            .contains("ADMIN");
	}

// 오토와이어드 하신 다음 클래스 호출 후 어드민 체크 쓰면 됩니다.
	
	
}
