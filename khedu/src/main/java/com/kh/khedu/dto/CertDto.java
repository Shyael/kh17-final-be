package com.kh.khedu.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CertDto {
	private String certEmail;
	private String certNumber;
	private Timestamp certTime;
	private String certYn;
	
	public boolean isComplete() {
		return certYn != null && certYn.equals("Y");
	}
}
