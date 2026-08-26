package com.kh.khedu.configuration;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
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
					SessionCreationPolicy.STATELESS //HTTP 세션을 인증 상태 유지 목적으로 사용하지 않겠다HTTP 세션을 인증 상태 유지 목적으로 사용하지 않겠다
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
					
						// 로그인만 필요한 경우
						.requestMatchers(
								//예시
								"/api/account/me" //내정보
						).authenticated()//인증필요	
						
						// 설정한 권한들 필요 
						//강의 security filter chain 1시간 29분 참조
						.requestMatchers(
								"/desk/info/**"
						).hasAuthority("학생조회")
						
						// 위 페이지 외에는 전부 거절
						.anyRequest().denyAll()
			)
			//JWT를 어떻게 검증할 것인지 설정 (JwtDecoder가 반드시 필요)
			//→ BearerTokenResolver :AccessToken을 꺼내서 Jwt를 뽑아내는 도구
			//→ JwtAuthenticationConverter : Jwt의 authority를 Spring Security용으로 변환
			
			//예외 상황 처리 설정
			//→ 인증되지 않은 경우는 401 , 권한이 부족한 경우는 403으로 반환하도록 설정
			.exceptionHandling(
				exception -> exception
					//인증되지 않은 경우
					.authenticationEntryPoint(
						(req, res, exp) -> res.setStatus(401)
					)
					//접근을 거부당한 경우
					.accessDeniedHandler(
						(req, res, exp) -> res.setStatus(403)
					)
			)
		;
		
		return http.build();
	}
	
	//CorsConfigurationSource 생성 (Security의 기본값으로 자동 설정)
	@Bean
	public CorsConfigurationSource configurationSource() {
		//설정 객체를 생성 //data가 클 경우에만 스트리밍방식을 사용(스트리밍 방식일 때, .reactive를 import)
		CorsConfiguration config = new CorsConfiguration();
		
		//CORS 설정 코드 작성
		//[1] 허용되는 접근 대상을 지정 (allow origins or pattern)
		config.setAllowedOrigins(List.of(
			// 여기에 운영주소 넣어주면됨
			"http://localhost:5173" 
		));
		//[2] 허용할 HTTP 메소드 설정
		config.setAllowedMethods(List.of(
				"GET", "POST", "PUT", "PATCH", "DELETE",
				//OPTIONS는 불확실한 상황일 때 보내는 사전 답사용 요청
				//불확실한 상황 : origin이 다른데 GET/HEAD가 아닌 요청을 보내면 불확실하다고 판단
				"OPTIONS",
				"HEAD"
		));
		//[3] 허용할 HTTP헤더 설정
		//→ 특정 헤더를 반드시 포함해야 하는 경우가 존재
		//→ 보안이 강화되면 CSRF 헤더만 허용하는 경우가 있음 (CSRF: 사이트간 요청 위조 방지 헤더)
		config.setAllowedHeaders(List.of("*"));
		//[4] 인증 쿠기 설정
		config.setAllowCredentials(true);
		//[5] preflight 시간 설정(캐싱 지속시간)
		config.setMaxAge(Duration.ofHours(1L)); //1시간 (=3600초, 기본값)
		
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
