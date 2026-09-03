package com.kh.khedu.error;

public class AlreadyExistsException extends RuntimeException {

	public AlreadyExistsException() {
		super();
	}

	public AlreadyExistsException(String message) {
		super(message);
	}

}