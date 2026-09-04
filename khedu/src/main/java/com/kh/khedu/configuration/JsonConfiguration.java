package com.kh.khedu.configuration;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//현재 프로젝트에서 JSON의 처리방침을 변경하기 위한 설정
//Spring에서는 Jackson Databind라는 기술에서 제공하는 ObjectMapper를 기본 변환도구로 사용
//기존의 ObjectMapper를 대체할 신규 ObjectMapper를 만들어서 교체 설정을 한다

@Configuration
public class JsonConfiguration {
	//@Bean
	public ObjectMapper objectMaper() {
		//신규 ObjectMapper 생성
		ObjectMapper mapper = new ObjectMapper();
		
		//- 1. Java 8+의 시간 방식 해석이 가능하도록 설정
		mapper.registerModule(new JavaTimeModule());
		
		//- 2. 문자열 해석 기준을 재설정 (공백은 null로 간주)
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new EmptyStringToNullDeserializer());
		mapper.registerModule(module);
		
		//반환
		return mapper;
	}
	
	//전체를 바꾸는 것이 아니라 특정 설정만 변경
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonMapperBuilder() {
		return builder -> {
			
			//String : 기존 도구 사용
			builder.deserializerByType(
					String.class, new EmptyStringToNullDeserializer()
			);
			
			//Integer
			builder.postConfigurer(mapper -> {
				mapper.coercionConfigFor(Integer.class)
						.setAcceptBlankAsEmpty(true)
						.setCoercion(//무엇을 → 어떤 값으로에 대한 설정
							CoercionInputShape.EmptyString, 
							CoercionAction.AsNull
						);
			});
			
			//JSON 날짜/시간 한국기준
			builder.timeZone(
			        TimeZone.getTimeZone("Asia/Seoul")
			);
		
		};
	}
	
	
}
