package com.kh.khedu.configuration;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

//empy String("")을 null로 번환해주는 도구

public class EmptyStringToNullDeserializer extends JsonDeserializer<String>{

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		//JsonParser가 변환도구
		//1. 현재 해석중인 값을 문자열로 불러온다
		String value = p.getText();
		
		//2.원래 null이거나, 공백을 제거해보니 비어있는 경우는 null로 치환
		if(value == null || value.strip().isEmpty()) {
			return null;
		}
		//3.나머지는 원래대로 반환
		return value;
	}
	
}
