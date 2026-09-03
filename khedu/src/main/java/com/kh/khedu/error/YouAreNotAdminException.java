package com.kh.khedu.error;

import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public class YouAreNotAdminException extends RuntimeException {
	public YouAreNotAdminException() {
		super();
		
	}
	
	public YouAreNotAdminException( String message) {
		super(message);
		
	}
}
