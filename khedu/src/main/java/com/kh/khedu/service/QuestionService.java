package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.vo.exam.StudentQuestionVO;

public interface QuestionService {
    // 문제 등록
	int insert(
	        QuestionDto questionDto,
	        List<MultipartFile> files,
	        int employeeNo,
	        boolean tutor
	) throws IllegalStateException, IOException;

    // 문제 단일 조회
    QuestionDto selectOne(int questionNo);

    // 특정 시험의 문제 목록 조회
    List<QuestionDto> selectListByExam(int examNo);
    
    //학생용 조회
    List<StudentQuestionVO> selectListByAttempt(
            int attemptNo,
            int studentNo
    );
    
    // 문제 수정
    boolean update(
            QuestionDto questionDto,
            List<MultipartFile> files,
            int employeeNo,
            boolean tutor
    ) throws IllegalStateException, IOException;

    // 문제 삭제
    boolean delete(
            int questionNo,
            int employeeNo,
            boolean tutor
    );

    // 문제 첨부파일 삭제
    void deleteFile(
            int questionNo,
            int attachNo,
            int employeeNo,
            boolean tutor
    );
}