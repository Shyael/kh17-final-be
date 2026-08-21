package com.kh.khedu.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//@Configuration
public class SecurityConfiguration {
	// 단방향 암호화를 위한 BCryptPasswordEncoder 등록
	@Bean
	public PasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http//Spring Security가 제공하는 http 설정 객체
	) throws Exception {
		//http에 홈페이지 운영 규칙을 모두 설정하고 Build해서 반환
		http	
			//cors 설정 : 별도로 등록한 CorsconfigurationSource의 설정을 따르겟다(없으면 기본값)
			.cors(Customizer.withDefaults())
			//session 설정 : 무상태(StateLess)로 설정
			.sessionManagement(
				session-> session.sessionCreationPolicy(
					SessionCreationPolicy.STATELESS //세션은 쓰긴쓰되 사용자 정보는 사용하지 않음
				)
			)
			//security의 기본 제공되는 로그인 화면과 인증시스템을 비활성화
			.formLogin(form->form.disable())
			.httpBasic(basic->basic.disable())
			.logout(logout->logout.disable())
//			.logout(AbstractHttpConfigurer::disable) //Java Method Reference
			
			//HTTP 요청에 대한 처리 계획
			//.requestMatchers("적용시킬 주소or패턴")
			// .permitAll() - 모두 수락(접속 허용)
			// .denyAll() - 모두 거절(접속차단)
			// .authenticated() - 인증 필요 (인증 방식에 대해서는 따로 정의)
			// .hasRole() - Spring security의 기본 역할 (`ROLE_`로 시작)
			// .hasAuthority() - 사용자가 임의로 지정한 역할
		
			.authorizeHttpRequests(
				auth -> auth	
					.requestMatchers(
						// 인증과 상관없이 허용가능 페이지
							"/active"  //체크용 페이지 허용
							,"/swagger-ui/**" //springdoc ui
							,"/v3/api-docs/**" //springdoc json
						).permitAll()
						// 조건부 허용(내가 만든 요소들)				
						.requestMatchers(
								//예시
								"/api/account/me" //sowjdqh
						).authenticated()//인증필요		
						// 위 페이지 외에는 전부 거절
						.anyRequest().denyAll()
			)
			//JWT를 어떻게 검증할 것인지 설정 (JwtDecoder가 반드시 필요)
			
			//예외 상황 처리 설정
			//→ 인증되지 않은 경우는 401 , 권한이 부족한 경우는 403으로 반환하도록 설정
		;
		
		return http.build();
	}
	
	//CorsConfigurationSource 생성 (Security의 기본값으로 자동 설정)
	@Bean
	public CorsConfigurationSource configurationSource() {
		//설정 객체를 생성
		CorsConfiguration config = new CorsConfiguration();
		
		//CORS 설정 코드 작성
		//[1] 허용되는 접근 대상을 지정 (allow origins)
		
		//적용시킬 주소까지 포함한 설정 객체로 확장
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		source.registerCorsConfiguration(
			"/**", //적용할 주소
			config //적용할 설정
		);
		
		//완성된 객체 반환
		return source;
	}
}
