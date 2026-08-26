package com.kh.khedu.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocConfiguration {
	
	//SpringDoc에 표시할 기본정보를 설정
	//(이 기술을 실제로 개발한 업체는 springfox swaager이기 때문에 패키지명에 유의
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info( info() ) //문서에 표시할 내부정보
				.externalDocs( externalDoc() ); //외부에 표시할 문서정보
	}
	
	private Info info() {
		return new Info()
				.title("연습용 REST API")
				.description("KH정보교육원 React+Spring Booots 수업자료")
				.version("v1.0.0")
				.contact(
					new Contact()
						.name("KH담당자")
						.email("admin@kh.com")
						.url("https://www.kh.com")
				)
				.license(
					new License()
						.name("MIT License")
						//오픈소스 => 교육용 학습용 버전 : MIT License // Apach License
						.url("https://opensource.org/license/mit")					
				);
	}
	
	private ExternalDocumentation externalDoc() {
		return new ExternalDocumentation()
					.description("깃허브 저장소")
					.url("https://github.com/hiphop5782");
	}
}