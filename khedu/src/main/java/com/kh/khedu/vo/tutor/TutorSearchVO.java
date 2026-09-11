package com.kh.khedu.vo.tutor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.khedu.util.PaginationVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(name="강사 검색용 VO")
@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class TutorSearchVO extends PaginationVO {
    //강사 이름 검색
    private String tutorName;
    //과목 검색
    private Integer academySubjectNo;
}