package com.kh.khedu.vo.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(name = "방 참여 요청 데이터")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class RoomEnterRequestVO {
	@NotNull @Positive 
	private int roomNo;
}