package com.kh.khedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //수납 청구서 발행용 스케쥴러
@SpringBootApplication
public class KheduApplication {

	public static void main(String[] args) {
		SpringApplication.run(KheduApplication.class, args);
	}

}
