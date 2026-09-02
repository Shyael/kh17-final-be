package com.kh.khedu.vo.student;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.vo.parentStudent.ParentStudentDetailVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="학생 개인정보 조회용")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentDetailVO {
	//student
	private int studentNo;
	private String studentSchool;
	private String studentGrade;
	private Timestamp studentCtime; // 학생 등록일
	private String studentAcademicStatus; // 대기/재원/휴원/퇴원/수료
	private Timestamp studentUtime; // 학생 정보 수정일
	// parent_student
	private Integer parentNo;
	private String relationship; // 부 모 보호자 기타
	// account
    private Timestamp accountUtime; // 학생 계정정보 수정일
    private int accountNo;
    private String accountId;
    private String accountName;
    private String accountBirth;
    private String accountPhone;
    private String accountStatus;
    private String accountType;
    
    //보호자 목록
    private List<ParentStudentDetailVO> parents;
}
