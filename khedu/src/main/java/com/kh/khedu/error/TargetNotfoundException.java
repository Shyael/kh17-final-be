package com.kh.khedu.error;

//대상을 찾을 수 없을 경우 404 처리를 위한 예외 클래스 (등록 안함
//public class TargetNotfoundException extends Exception { //unchecked exception
	public class TargetNotfoundException extends RuntimeException { //checked exception
		public TargetNotfoundException() {
			super();
		}
		public TargetNotfoundException(String message) {
			super(message);
		}
}
