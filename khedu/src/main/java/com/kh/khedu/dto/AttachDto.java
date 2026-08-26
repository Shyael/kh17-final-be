package com.kh.khedu.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="첨부파일 DTO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttachDto {
	private int attachNo;
	private String attachName;
	private String attachType;
	private long attachSize;
	private String attachRename;
	
	//파일 유형을 알려주기 위한 메소드
	public String getAttachTypeString() {
		if(attachType == null) {
			return "application/octet-stream";
		}
		return attachType;
	}
	
}

