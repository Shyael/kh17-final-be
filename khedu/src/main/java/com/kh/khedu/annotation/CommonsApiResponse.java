package com.kh.khedu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

//내가 만드는 커스텀 어노테이션
//1. 이 어노테이션을 설정할 수 있는 위치를 지정 (메소드, 클래스, 또다른 어노테이션에 설정 가능)
@Target(
	{
		ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE
	})
//2. 이 어노테이션이 실질적으로 작동하는 시점 (실행중에도 읽어낼 수 있도록 설정)
@Retention(RetentionPolicy.RUNTIME)
//3. 자동으로 생성되는 API에 이 내용이 포함되도록 설정(관례적으로 커스텀 파일에 작성)
@Documented
//4. 내가 부여하고 싶은 모든 애노테이션을 이곳에 작성
@ApiResponses({
	@ApiResponse(
		responseCode = "404",
		description = "대상을 찾을 수 없음",
		content = @Content(
			mediaType = "text/plain",
			schema = @Schema(
				implementation = String.class, 
				example = "Target not found"
			)
		)
	),
	@ApiResponse(
		responseCode = "500",
		description = "서버 내부 오류",
		content = @Content(
			mediaType = "text/plain",
			schema = @Schema(
				implementation = String.class, 
				example = "Server error"
			)
		)
	)
})
public @interface CommonsApiResponse {
	
}