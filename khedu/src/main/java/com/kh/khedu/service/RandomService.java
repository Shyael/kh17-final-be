package com.kh.khedu.service;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class RandomService {
	//목적: 랜덤과 관련된 처리를 하기 위해서
	
	private Random r = new Random();
	
	private String numbers = "0123456789";
	private String lowerCases = "abcdefghijklmnopqrstuvwxyz";
	private String upperCases = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String special = "!@#$%^&*()-_+=";
	
	//number 생성 //자릿수가 정해져있을 때 만들기 쉬운 방식
	public String generateNumber(int size) {
		StringBuffer buffer = new StringBuffer(); //버퍼 생성
		for(int i=0; i < size; i++) { // 너가 입력한 숫자만큼 size번 
			int index = r.nextInt(numbers.length()); //위치선정
			char ch = numbers.charAt(index); //해당 위치 글자 추출
			buffer.append(ch);  //버퍼에 추가
		}
		return buffer.toString(); //반환
	}
	
	//문자열 생성
	public String generateString(int size) {
		StringBuffer buffer = new StringBuffer();
				
		for(int i = 0; i <size; i++) {
			//종류 선택
			int type = r.nextInt(4);
			
			//[1] switch구문으로 비밀번호 생성
	//		String target;
	//		switch(type) {
	//		case 0 : target = numbers; break;
	//		case 1 : target = lowerCases; break;
	//		case 2 : target = upperCases; break;
	//		case 3 : target = special; break;
	//		default : target = special;
	//		}
				
				//[2] switch yield 구문으로 비밀번호 생성
	//		String target = switch(type) {
	//		case 0 : yield numbers;
	//		case 1 : yield lowerCases;
	//		case 2 : yield upperCases;
	//		default : yield special;
	//		};
			
			//[3] 최종!!!! Java 13+에서 사용 가능한 switch var 구문 (람다 사용)
			String target = switch(type) {
			case 0 -> numbers;
			case 1 -> lowerCases;
			case 2 -> upperCases;
			default -> special;
			};
			
			//종류에 따른 글자 선택
			int position = r.nextInt(target.length()); // 마지막글자
			
			//추가
			buffer.append(target.charAt(position));
		}
		
		return buffer.toString();
	}
	
	// 임시 비밀번호 생성
	public String generatePassword(int size) {
	    StringBuffer buffer = new StringBuffer();

	    // 최소 조건 보장
	    // 1. 대문자
	    buffer.append(
	        upperCases.charAt(r.nextInt(upperCases.length()))
	    );

	    // 2. 소문자
	    buffer.append(
	        lowerCases.charAt(r.nextInt(lowerCases.length()))
	    );

	    // 3. 특수문자
	    buffer.append(
	    		special.charAt(r.nextInt(special.length()))
	    );

	    // 4. 나머지는 숫자
	    for(int i = 0; i < size - 3; i++) {
	        buffer.append(
	            numbers.charAt(r.nextInt(numbers.length()))
	        );
	    }

	    return buffer.toString();
	}
	
	// 연동 코드 생성
	public String generateLinkCode() {
		String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
		SecureRandom RANDOM = new SecureRandom();

		StringBuilder code = new StringBuilder();
		
		for(int i = 0; i < 6; i++) {
	        int index = RANDOM.nextInt(chars.length());
	        code.append(chars.charAt(index));
	    }
		
		return code.toString();
	}
}
