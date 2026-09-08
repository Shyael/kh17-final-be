package com.kh.khedu.vo.course;

import java.util.List;

import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.dto.ClassroomDto;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;
import com.kh.khedu.vo.grade.GradeVO;
import com.kh.khedu.vo.tutor.TutorSubjectVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="강좌 등록 총체 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseFormDataVO {
	private List<AcademySubjectDto> subjectList;
	private List<GradeVO> gradeList;
	private List<ClassroomWhenRegisterVO> classroomList;
	private List<TutorSubjectVO> tutorList;
}
