package com.kh.khedu.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

//application.properties에 있는 custom.email 속성을 저장시킬 파일
@Data //lombok(why? setter메소드 필요)
@Component //외부에서 @Autowired 할 수 있도록 등록
@ConfigurationProperties(prefix = "custom.email") // 설정값을 복사해서 가져와야함을 명시
public class EmailProperties {
	private String host; //접두사(prefix) + host라는 항목을 읽어서 여기에 저장해!
	private int port; //접두사(prefix) + port라는 항목을 읽어서 여기에 저장해!
	private String username; //접두사 + username라는 항목 읽어서 저장
	private String password; //접두사 + password라는 항목을 읽어서 저장
	private String from;
}
