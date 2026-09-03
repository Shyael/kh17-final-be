package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dao.QuestionDao;
import com.kh.khedu.dto.QuestionDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AttachService attachService;

    // 문제 등록
    @Override
    public int insert(
            QuestionDto questionDto,
            List<MultipartFile> files
    ) throws IllegalStateException, IOException {
        // 문제 번호 생성
        int questionNo = questionDao.sequence();
        questionDto.setQuestionNo(questionNo);
        
        // 문제 등록
        questionDao.insert(questionDto);
        
        // 첨부파일 등록
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // attach DB + 실제 파일 저장
                    int attachNo = attachService.save(file);
                    // 문제와 첨부파일 연결
                    questionDao.connect(questionNo, attachNo);
                }
            }
        }

        return questionNo;
    }

    // 문제 단일 조회
    @Override
    public QuestionDto selectOne(int questionNo) {

        QuestionDto questionDto = questionDao.selectOne(questionNo);
        
        if (questionDto == null) {
            throw new TargetNotfoundException();
        }

        return questionDto;
    }

    // 특정 시험의 문제 목록 조회
    @Override
    public List<QuestionDto> selectListByExam(int examNo) {
        return questionDao.selectListByExam(examNo);
    }

    // 문제 수정
    @Override
    public boolean update(
            QuestionDto questionDto,
            List<MultipartFile> files
    ) throws IllegalStateException, IOException {

        // 문제 존재 확인
        QuestionDto findQuestion = questionDao.selectOne(questionDto.getQuestionNo());
        
        if (findQuestion == null) {
            throw new TargetNotfoundException();
        }

        // 문제 기본정보 수정
        boolean result = questionDao.update(questionDto);

        // 신규 첨부파일 추가
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    int attachNo = attachService.save(file);
                    questionDao.connect(questionDto.getQuestionNo(), attachNo);
                }
            }
        }
        
        return result;
    }

    // 문제 삭제
    @Override
    public boolean delete(int questionNo) {

        // 문제 존재 확인
        QuestionDto questionDto = questionDao.selectOne(questionNo);

        if (questionDto == null) {
            throw new TargetNotfoundException();
        }

        // 문제 첨부파일 번호 미리 조회
        List<Integer> fileNos =questionDao.selectFiles(questionNo);

        // 문제 삭제
        boolean result = questionDao.delete(questionNo);

        // attach DB + 실제 파일 삭제
        for (Integer attachNo : fileNos) {
            attachService.delete(attachNo);
        }
        
        return result;
    }

    // 문제 첨부파일 삭제
    @Override
    public void deleteFile(
            int questionNo,
            int attachNo) {

        // 해당 문제에 연결된 파일인지 확인
        List<Integer> fileNos = questionDao.selectFiles(questionNo);

        if (!fileNos.contains(attachNo)) {
            throw new GetOutException();
        }

        // attach DB + 실제 파일 삭제
        attachService.delete(attachNo);
    }
}