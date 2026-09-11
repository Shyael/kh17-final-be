package com.kh.khedu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학부모 Dto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentDto {
	private int parentNo;
	private int accountNo;
}
