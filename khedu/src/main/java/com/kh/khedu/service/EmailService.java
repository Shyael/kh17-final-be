package com.kh.khedu.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kh.khedu.configuration.EmailProperties;
import com.kh.khedu.dao.AccountDao;
import com.kh.khedu.dao.CertDao;
import com.kh.khedu.dto.CertDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private RandomService randomService;
	
	@Autowired
	private CertDao certDao;
	
	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private EmailProperties emailProperties;
	
	//이 메소드는 이제부터 비동기(백그라운드,멀티스레드)로 실행된다고 선언!
	@Async
	public void sendWelcomeMail(String memberEmail) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("tookwak4@gmail.com");
		message.setTo(memberEmail);
		message.setSubject("[KH정보교육원] 가입을 진심으로 환영합니다!");
		message.setText("앞으로도 많은 활동 부탁드립니다!");
		sender.send(message);
	}
	
	//인증번호 발송 메소드 (단문메일용)
	public void sendCertNumber(String memberEmail) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("tookwak4@gmail.com");
		message.setTo(memberEmail);
		message.setSubject("[KH정보교육원] 인증코드가 도착하였습니다!");
		
		//인증번호 생성(랜덤으로)
		String number = randomService.generateNumber(6);
		message.setText("인증번호는 [" + number + "] 입니다. \n입력창에 입력 후 확인을 눌러주세요");
		
		//이메일 발송
		sender.send(message);
		
		//발송이 되었다면, DB등록 혹은 갱신처리
		CertDto certDto = certDao.find(memberEmail);
		if(certDto == null) { //처음 보내는 이메일
			certDao.add(CertDto.builder()
						.certEmail(memberEmail)
						.certNumber(number)
					.build());
		}
		else { //이미 보낸적이 있는 이메일
			certDao.change(CertDto.builder()
					.certEmail(memberEmail)
					.certNumber(number)
				.build());
		}
	}
	
	//인증번호 발송 메소드 (마임메세지용)
	public void sendCertNumber2(String memberEmail) throws MessagingException, IOException {
		//SimpleMailMessage message = new SimpleMailMessage();
		MimeMessage message =sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
		
		helper.setFrom(emailProperties.getFrom());
		helper.setFrom("tookwak4@gmail.com");
		helper.setTo(memberEmail);
		helper.setSubject("[KH정보교육원] 인증코드가 도착하였습니다!");
		
		//인증번호 생성(랜덤으로)
		String number = randomService.generateNumber(6);
		
		//HTML 템플릿 생성
		String template = this.createCertHtml(number);
		
		helper.setText(template, true);
		
		//이메일 발송
		sender.send(message);
		
		//발송이 되었다면, DB등록 혹은 갱신처리
		CertDto certDto = certDao.find(memberEmail);
		if(certDto == null) { //처음 보내는 이메일
			certDao.add(CertDto.builder()
						.certEmail(memberEmail)
						.certNumber(number)
					.build());
		}
		else { //이미 보낸적이 있는 이메일
			certDao.change(CertDto.builder()
					.certEmail(memberEmail)
					.certNumber(number)
				.build());
		}
	}
	
	public String createCertHtml(String certNumber) throws IOException {
		//뽑아내서 자바io형태로 저장
				ClassPathResource resource = 
						new ClassPathResource("templates/cert-template.html"); //src제외한 나머지 경로
				File target = resource.getFile();
				
				
//				파일을 읽을 준비
				BufferedReader reader = new BufferedReader(new FileReader(target));
				
//				StringBuffer를 이용해서 합성해서 전송
				StringBuffer buffer = new StringBuffer();
				
//				한 줄씩 읽어와서 합성
				while(true) {
					String line = reader.readLine(); //한 줄씩 읽어서
					if(line == null) break; // EOF발견 시 탈출
					buffer.append(line); // 버퍼에 추가
				}
				
				reader.close(); //사용을 완료한 통로 정리
				
				//문자열로 뽑아내는 것까지는 기존 예제와 동일
				String html = buffer.toString();
				
				//Jsoup이란 기술을 이용해서 문자열을 html로 변환한 뒤 원하는 태그를 찾아 변조
				Document document = Jsoup.parse(html);
				
				//var list = $(".number-wrapper"); // jquery였다면
				Elements list = document.select(".number-wrapper"); //number-wrapper 클래스를 찾고 
				
				for(int i =0; i < list.size(); i++) { // 반복해서
					Element tag = list.get(i); // 태그정보를 알아낸뒤
					char ch = certNumber.charAt(i); // 인증번호 한 자리를 뽑아서
					tag.text(String.valueOf(ch)); // 설정
				}
				return document.toString();
	}
	
	//임시비밀번호 발송하는 서비스
//	public void sendTempPassword(String email, String tempPassword) throws MessagingException, IOException {
//		//SimpleMailMessage message = new SimpleMailMessage();
//		MimeMessage message =sender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
//		helper.setFrom("tookwak4@gmail.com");
//		helper.setTo(email);
//		helper.setSubject("[KH정보교육원] 임시비밀번호가 도착하였습니다!");
//		
//		//인증번호 생성(랜덤으로)
//		String number = randomService.generateNumber(12);
//		System.out.println("임시비번 : " + number);
//		//HTML 템플릿 생성
//		String template = this.createCertHtml(number);
//		
//		helper.setText(template, true);
//		
//		//이메일 발송
//		sender.send(message);
//	}
	
	//임시비밀번호 발송하는 서비스
	public void sendTempPassword(String email, String tempPassword) throws MessagingException, IOException {
//		//단문 메세지
//		SimpleMailMessage message = new SimpleMailMessage();
//		
//		message.setFrom(emailProperties.getFrom());
//		message.setTo(email);
//		message.setSubject("[KH정보교육원] 임시 비밀번호 안내");
//		message.setText("임시 비밀번호는 ["+ tempPassword +"] 입니다. \n" + "외부에 노출되지 않도록 주의하세요");
		
		//마임 메세지
		ClassPathResource resource = new ClassPathResource(
				"templates/temp-password-template.html");//src제외한 나머지 경로탐색
		File target = resource.getFile();
//		BufferedReader reader = new BufferedReader(new FileReader(target)); //애초부터 systemREader을 통해 읽어옴 읽는 도구를 선택할 수 없음
		BufferedReader reader = new BufferedReader(
				//내가 바이트 단위로 읽을 건데  (읽는 도구 선택가능) // 위 방식으로 하면 깨질 수 있는 위험이 있을 수도 있기 때문에 아래와 같이 내가 명시할 수 있는 코드로 작성
				new InputStreamReader(new FileInputStream(target), StandardCharsets.UTF_8
				)
		);
				
		String content = reader.lines() 
				.collect( //합쳐
					Collectors.joining( //콜랙테에서 
						System.lineSeparator()) // /n 줄바꿈 표시를 찾아서 
				);
		
		//구글에 데이터가 깨져서 들어가서 확인해 보기위해 찍은 코드
//		System.out.println("<Content>");
//		System.out.println(content);
		
		reader.close();
		
		//String → HTML
		Document document = Jsoup.parse(content);
		Elements boxes = document.select(".password-text"); // 무조건 1개
		Element element = boxes.get(0); //boxes.getFirst();
		element.text(tempPassword);
		System.out.println("임시 비밀번호 " + tempPassword);
		//메세지 생성 및 전송
		MimeMessage  message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
		helper.setFrom(emailProperties.getFrom());
		helper.setTo(email);
		helper.setSubject("[KH정보교육원] 임시 비밀번호 안내");
		helper.setText(document.toString(), true); //HTML모드
		
		sender.send(message);
	}
	

}
