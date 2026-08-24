package com.kh.khedu.error;


// 비회원이 회원 기능에 접근하면 발생하는 예외
//public class WhoAreYouException extends Exception{ // 이 예외는 반드시 try-catch 처리가 필요
public class WhoAreYouException extends RuntimeException{

	public WhoAreYouException() {
		super();
	}

	public WhoAreYouException(String message) {
		super(message);
	} // 이 예외는 처리를 생략할 수 있다
	
	
	
}
