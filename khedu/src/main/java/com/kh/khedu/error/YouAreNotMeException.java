package com.kh.khedu.error;

public class YouAreNotMeException extends RuntimeException {
	public YouAreNotMeException() {
		super();
	}

	public YouAreNotMeException(String message) {
		super(message);
	} // 이 예외는 처리를 생략할 수 있다
	
}
