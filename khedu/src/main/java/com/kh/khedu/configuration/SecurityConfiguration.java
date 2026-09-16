package com.kh.khedu.configuration;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kh.khedu.enums.RoleType;

import jakarta.servlet.http.Cookie;

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
		,BearerTokenResolver bearerTokenResolver // 내가 만든 토큰해석기
		,JwtAuthenticationConverter jwtAuthenticationConverter // 내가 jwt에서 만든 권한을 securityfilterChain에 맞게 변환
	) throws Exception {
		//http에 홈페이지 운영 규칙을 모두 설정하고 Build해서 반환
		http
			//csrf 비활성화
			.csrf(csrf -> csrf.disable())
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
						// [0] React SPA 정적 리소스 및 프론트엔드 화면 경로 무조건 허용 (최상단 배치 필수)
						.requestMatchers(
							"/",
							"/index.html",
							"/assets/**",
							"/favicon.ico",
							"/*.svg",
							"/*.png",
							"/*.ico",
							"/error",
							"/employee/**" // React의 employee 관련 모든 화면 경로 허용
						).permitAll()

						// [기존] 무조건 허용 API
						.requestMatchers(
							"/active",  //체크용 페이지 허용
							"/swagger-ui/**", //springdoc ui
							"/v3/api-docs/**", //springdoc json
							"/api/admin/employee/**",
							"/api/account/check-id/**",
							"/ws/**", 
							"/ws-member/**"
						).permitAll()
							
						//직원(학원 정보 수정)
						.requestMatchers("/api/academy/**").permitAll()
						.requestMatchers("/api/employe/**").permitAll()
						//직원(강사 정보 수정)
						.requestMatchers("/api/tutor/**").permitAll() 
						
						//auth service
						.requestMatchers(
							"/service/auth/login", //로그인 페이지
							"/service/auth/logout", //로그아웃 페이지
							"/service/auth/refresh", //로그인 갱신페이지
							"/api/account/find-id", //아이디
							"/api/account/find-password" //비밀번호 찾기
						).permitAll()
						
						//임시 전부 공개화면 
						.requestMatchers(
							"/api/employee/**" // 원장, 데스크만 접근 가능하게
						).permitAll()

						//cert service
						.requestMatchers("/service/cert/**").permitAll()
						
						//계약 관련(임시)
						.requestMatchers("/api/contract/**").permitAll()
						//공개 화면
						.requestMatchers(
							"/academy/**",
							"/api/student/**",
							"/api/parent/**"
						).permitAll() 
					
						// 조건부 허용(내가 만든 요소들)
						
						// [1] 회원
						.requestMatchers("/api/account/**")
						.hasAnyAuthority(
							RoleType.STUDENT.getCode(),
							RoleType.PARENT.getCode(),
							RoleType.TUTOR.getCode(), 
							RoleType.DESK.getCode(),
							RoleType.ADMIN.getCode()
						)
						
						// [2] 학생
						.requestMatchers("/api/academy/assignment/student/**")
						.hasAnyAuthority(RoleType.STUDENT.getCode())
						
						// [3] 학부모
						.requestMatchers("/api/academy/assignment/parent/student/**")
						.hasAnyAuthority(RoleType.PARENT.getCode())
						
						// [4] 데스크 / 원장 / 직원
						.requestMatchers(
							"/api/employee/**",
							"/api/attendance/**" //근태관련
						)
						.hasAnyAuthority(
							RoleType.TUTOR.getCode(), 
							RoleType.DESK.getCode(),
							RoleType.ADMIN.getCode()
						)
						.requestMatchers("/api/admin/employee/**")
						.hasAnyAuthority(
							RoleType.DESK.getCode(),
							RoleType.ADMIN.getCode()
						)

						//나머지 모두 허용
						.anyRequest().permitAll()
				)
			//JWT를 어떻게 검증할 것인지 설정 (JwtDecoder가 반드시 필요)
			//→ BearerTokenResolver :AccessToken을 꺼내서 Jwt를 뽑아내는 도구
			//→ JwtAuthenticationConverter : Jwt의 authority를 Spring Security용으로 변환
			.oauth2ResourceServer(
					oauth2 -> 	oauth2
						//하단에 @Bean으로 만든 해석도구를 oauth2의 표준 해석기로 설정
						.bearerTokenResolver(bearerTokenResolver)
						//하단에 @Bean으로 만든 JWT 권한 해석 및 변환기를 설정
						.jwt(
							jwt -> jwt.jwtAuthenticationConverter(
									jwtAuthenticationConverter //내가 만든 도구
							)
						)
						
				)
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
	public CorsConfigurationSource corsConfigurationSource() {
		//설정 객체를 생성 //data가 클 경우에만 스트리밍방식을 사용(스트리밍 방식일 때, .reactive를 import)
		CorsConfiguration config = new CorsConfiguration();
		
		//CORS 설정 코드 작성
		//[1] 허용되는 접근 대상을 지정 (allow origins or pattern)
		config.setAllowedOrigins(List.of(
			// 여기에 운영주소 넣어주면됨
			"http://localhost:5173",
			"http://13.125.227.139:8080"
		));
		//[2] 허용할 HTTP 메소드 설정
		config.setAllowedMethods(List.of(
				"GET", "POST", "PUT", "PATCH", "DELETE",
				//OPTIONS는 불확실한 상황일 때 보내는 사전 답사용 요청
				//불확실한 상황 : origin이 다른데 GET/HEAD가 아닌 요청을 보내면 불확실하다고 판단
				"OPTIONS",
				//HEAD는 GET과 같은데 응답 본문을 가져오지 않는 요청방식
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
	
	//BearerTokenResolver
		// - Bearer는 토큰의 한 종류 (인증을 통해 무언가를 얻어내겠다는 의미의 토큰)
		// - 토큰은 표준이 없어서 JWT앞에 어떤 접두사를 붙여도 무방
		// - 헤더 방식인 경우 "Authorization: Bearer [토큰값]" 과 같은 형태로 전달
		// - 카카오는 KAKAOAK 라는 자체 이름을 만들어서 토큰에 적용하여 사용하고 있음 (즉, 자율적)
		// - 인증용 토큰을 해석하는 도구(accessToken 쿠키)
		@Bean
		public BearerTokenResolver bearerTokenResolver() {
			return request -> {
				String path = request.getServletPath();
				
				// 1. API 요청(/api/**, /service/**)이 아니면 토큰을 추출하지 않음
		        // 즉, /employee/** 화면 진입, 정적 리소스 등은 토큰 검사를 아예 건너뛰고 화면을 바로 띄움
		        if (!path.startsWith("/api/") && !path.startsWith("/service/") && !path.startsWith("/ws")) {
		            return null;
		        }
		        
		        // 2. 비로그인/공개 API는 브라우저에 만료된 쿠키가 남아있어도 무시(null 반환)
		        if (
		            path.startsWith("/service/auth/") ||          // 인증 관련 하위 전체
		            path.startsWith("/service/cert/") ||          // 이메일 인증 하위 전체
		            path.equals("/academy") || path.startsWith("/academy/") ||
		            
		            // 학생/학부모 회원가입 및 공개 경로 (/api/student, /api/student/ 등)
		            path.equals("/api/student") || path.startsWith("/api/student/") ||
		            path.equals("/api/parent") || path.startsWith("/api/parent/") ||
		            
		            // 계정 찾기 및 중복체크
		            path.startsWith("/api/account/check-id/") || path.equals("/api/account/check-id") ||
		            path.equals("/api/account/find-id") ||
		            path.equals("/api/account/find-password")
		        ) {
		            return null;
		        }
				
				//accessToken이 필요한 주소만 남았으므로 검색을 통해 찾아서 반환
				Cookie[] cookies = request.getCookies();//모든 쿠키를 긁어온다
				if(cookies == null) { //options(불확실한 요청 : 남의 서버에 get이 아닌 요청) 같은 상황에서 null일 수 있다
					return null; 
				}
				
//				모던 자바(Stream API)버전으로 쿠키 찾기
				return Arrays.stream(cookies)
						.filter(cookie -> cookie.getName().equals("accessToken"))
						.map(cookie -> cookie.getValue())
						.filter(value -> value != null && !value.isBlank())
						.findFirst()
						.orElse(null);
			};
		}
		
		//JwtAuthenticationConverter
		// - JWT의 authorities 항목을 Spring Security Authority로 변환하는 역할
		@Bean
		public JwtAuthenticationConverter jwtAuthenticationConverter() {
			
			//권한 정보 변환 도구 생성
			JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
			
			//jwt에서 authorities와 관련된 claim 이름을 설정
			converter.setAuthoritiesClaimName("authorities");
			
			//기본 접두사 (ROLE_, SCOPE_)를 모두 제거
			converter.setAuthorityPrefix(""); //접두사 없음
			
			//최종 JWT 변환 도구를 생성	
			JwtAuthenticationConverter result = new JwtAuthenticationConverter();
			
			result.setJwtGrantedAuthoritiesConverter(jwt -> {

		        Collection<GrantedAuthority> authorities =
		                converter.convert(jwt);

		        return authorities;
		    });
			
			//앞서 만든 도구를 장착
			result.setJwtGrantedAuthoritiesConverter(converter);
			
			//반환
			return  result;
		}
}
