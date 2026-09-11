package com.kh.khedu.vo.auth;

import java.util.List;

import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="로그인 응답 API")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthLoginResponseVO {
	private int accountNo;
	private String accountId;
	private String accountName;
	private String accountType;
	private List<String> roleNames;
	private Integer typeNo; //단순 계정등록만 된 회원일 수 있기 때문에 integer
	
	//학부모일 경우
	private List<ParentStudentDetailVO> children;
}
