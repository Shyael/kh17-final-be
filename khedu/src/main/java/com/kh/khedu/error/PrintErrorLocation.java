package com.kh.khedu.error;

public class PrintErrorLocation {
	public void printGetOutLocation(GetOutException e) {

	    StackTraceElement location =
	            e.getStackTrace()[0];

	    System.err.println(
	            "===== GetOutException 발생 ====="
	    );

	    System.err.println(
	            "클래스 : "
	            + location.getClassName()
	    );

	    System.err.println(
	            "메소드 : "
	            + location.getMethodName()
	    );

	    System.err.println(
	            "파일 : "
	            + location.getFileName()
	    );

	    System.err.println(
	            "라인 : "
	            + location.getLineNumber()
	    );

	    System.err.println(
	            "=============================="
	    );
	}
}
