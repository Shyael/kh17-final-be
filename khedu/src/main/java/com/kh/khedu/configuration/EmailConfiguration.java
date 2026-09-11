package com.kh.khedu.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//이메일 발송에 관련된 도구들을 등록해두는 설정파일
@Configuration
public class EmailConfiguration {
	@Autowired
	private EmailProperties emailProperties;
	
	@Bean //등록될 객체를 만드는 어노테이션
	public JavaMailSenderImpl sender() {
		// 메일 전송 도구 생성
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		
		// 메일 전송 도구의 정보 설정
		sender.setHost(emailProperties.getHost()); //이용할 업체의 호스트 정보
		sender.setPort(emailProperties.getPort()); //이용할 업체의 포트 번호
		sender.setUsername(emailProperties.getUsername()); //이용자의 계정이름
		sender.setPassword(emailProperties.getPassword()); //이용자의 앱 비밀번호
		
		Properties props = new Properties(); //Map<String, String> 형태 (문자열만 작성가능)
		props.setProperty("mail.smtp.auth", "true"); // 인증 사용 (강제 true아니면 이메일 안보내줌)
		props.setProperty("mail.smtp.debug", "true"); // 에러 발생 시 통신내역 출력(운영단계에서는 false 개발단계 true) why? 너무많은 로그가 생김 //
		props.setProperty("mail.smtp.starttls.enable", "true"); // 강제 보안 프로토콜 사용
		props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2"); // 보안 프로토콜 버전을 최신으로 지정
		props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com"); // 신뢰할 수 있는 업체 목록에 추가(자바의 기본 보안검사를 명시적으로 100% 통과시킴 안할 시 차단 당할수도 안당할수도) 
		
		//상세 옵션 설정
		sender.setJavaMailProperties(props);
		
		return sender; //다 만들었으니까 가져가서 등록하세요
	}
}
