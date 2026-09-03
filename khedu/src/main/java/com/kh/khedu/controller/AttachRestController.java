package com.kh.khedu.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.configuration.StorageProperties;
import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.service.AttachService;
import com.kh.khedu.vo.attach.AttachInfoVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Tag(name = "첨부파일 API")
@CommonsApiResponse

@Slf4j
@RestController
@RequestMapping("/api/attach")
public class AttachRestController {

	@Autowired
	private AttachService attachService;
	
	@Autowired
	private AttachDao attachDao;
	
	@Autowired
	private S3Presigner s3Presigner;
	
	@Autowired
	private StorageProperties storageProperties;
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/{attachNo}")
	public ResponseEntity<?> download(
			@PathVariable int attachNo
			) throws IOException{
		log.debug("현재 cloud Profile인가?? = {}", environment.matchesProfiles("cloud"));
		log.debug("현재 local Profile인가?? = {}", environment.matchesProfiles("local"));
		if(environment.matchesProfiles("cloud")) {
			return ResponseEntity.status(302)
						.location(URI.create("./p/"+attachNo))
					.build();
		}
		
		//[1] AttachService를 이용해서 파일과 파일 정보를 부른다
		AttachInfoVO vo = attachService.load(attachNo);
		
		//[2] 조회 결과를 이용해서 사용자에게 보낼 다운로드용 응답을 내보낸다
		//-> 다운로드 형식은 우리가 어찌할 수 없는 정해진 웹 통신 규격
		return ResponseEntity.ok()
				//헤더
				.header(HttpHeaders.CONTENT_ENCODING, "UTF-8")  //
				.header(HttpHeaders.CONTENT_TYPE, vo.getAttachDto().getAttachTypeString())
				
				.contentLength(vo.getAttachDto().getAttachSize())
				
				//.header("Content-Disposition", "attachment; filename=000") //기존 파일명 띄어쓰기 한글명 처리가 안되었음(원래 통신은 UNICODE가 넘어가지 않음)
				.header(
					"Content-Disposition", 
					ContentDisposition
						.attachment()
						.filename(
								vo.getAttachDto().getAttachName(),
								StandardCharsets.UTF_8
						)
						.build().toString()
				)
				//바디
				.body(vo.getResource());
	}
	
	@GetMapping("/p/{attachNo}")
	public ResponseEntity<?> presigned(@PathVariable int attachNo){
		// 파일 정보 조회
		AttachDto attachDto = attachDao.selectOne(attachNo);
		if(attachDto == null) throw new TargetNotfoundException();
		
		System.out.println(storageProperties);
		String objectKey = storageProperties.getAwsRoot() +"/"+ attachNo;
		
		GetObjectRequest request = GetObjectRequest.builder()
					.bucket(storageProperties.getAwsBucket())
					.key(objectKey)
					.responseContentDisposition(
							ContentDisposition
							.attachment()
							.filename(
									attachDto.getAttachName(),
									StandardCharsets.UTF_8
							)
							.build().toString()
					)
				.build();
		
		//Request를 Presigner로 한번 더 포장해서 전송
		GetObjectPresignRequest presignRequest = 
				GetObjectPresignRequest.builder()
					.signatureDuration(
							Duration.ofMinutes(storageProperties.getPresignedLimit())
					) // 보통 10분~15분 권장
					.getObjectRequest(request)
				.build();
		
		//이 코드로 우리가 얻어내고 싶은건 S3가 발급한 임시 다운로드 주소
		String url = s3Presigner.presignGetObject(presignRequest)
					.url().toString();

		//성공하면 200이 아니라 302번 응답을 발생시켜서 S3 Presigned URL로 이동시켜야 한다
		return ResponseEntity
				.status(302)
				.location(URI.create(url))//상대
			.build();
	}
}
