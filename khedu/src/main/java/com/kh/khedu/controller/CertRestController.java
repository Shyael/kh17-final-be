package com.kh.khedu.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.cert.CertCheckRequestVO;
import com.kh.khedu.cert.CertCheckResponseVO;
import com.kh.khedu.cert.CertSendRequestVO;
import com.kh.khedu.dao.CertDao;
import com.kh.khedu.dto.CertDto;
import com.kh.khedu.service.EmailService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;

@Tag(name = "이메일 발송 서비스")
@CommonsApiResponse

@RestController
@RequestMapping("/service/cert") // restapi는 원래 구정이 없기 때문에 실무에서는 ./cert로 보통 작성
public class CertRestController {
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private CertDao certDao;
	
	@ApiResponse(responseCode = "200", description = "이메일 발송 성공")
	@PostMapping("/send")
	public void send(@RequestBody CertSendRequestVO vo) throws MessagingException, IOException{
		emailService.sendCertNumber2(vo.getCertEmail());
	}
	
	@ApiResponse(responseCode = "200", description = "이메일 검사 성공")
	@PostMapping("/check")
	public CertCheckResponseVO check(@RequestBody CertCheckRequestVO vo) {
		
		//[1] 정보가 있는 지 확인
		CertDto findDto = certDao.find(vo.getCertEmail());
//				if(findDto == null) throw new WhoAreYouException(); //에러로 처리 
		if(findDto == null) {
			return CertCheckResponseVO.builder()
					.valid(false)
				.build();
		}
		
		//2. 번호가 맞는지 확인
		boolean valid = vo.getCertNumber().equals(findDto.getCertNumber());
		if(valid == false) {
			return CertCheckResponseVO.builder()
					.valid(false)
				.build();
		}

		//[2] 시간이 유효한 지 확인(현재시간과 보낸시간을 확인)
		LocalDateTime current = LocalDateTime.now(); //현재시각
		LocalDateTime sent = findDto.getCertTime().toLocalDateTime(); //발송시각
		Duration duration = Duration.between(sent, current);
		if(duration.toMinutes() > 10) { //10분이 지났다면
			return CertCheckResponseVO.builder()
					.valid(false)
				.build();
		}
			
		
		//[4] 인증 가능한 상태인지 확인(cert_yn이 N인 경우)
		//if(findDto.getCertYn().equals("Y")) {
		if(findDto.isComplete()) {
			return CertCheckResponseVO.builder()
					.valid(false)
				.build();
		}
		
		//certDao.delete(certDto.getCertEmail()); // 사용한 인증번호 지우기!
		certDao.use(vo.getCertEmail()); //인증완료(cert_yn='Y')로 업데이트
		{
			return CertCheckResponseVO.builder()
					.valid(true)
					.build();
		}
	}
	
}
