package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.QuestionOptionDao;
import com.kh.khedu.dto.QuestionOptionDto;
import com.kh.khedu.error.TargetNotfoundException;

@Service
@Transactional
public class QuestionOptionServiceImpl
        implements QuestionOptionService {

    @Autowired
    private QuestionOptionDao questionOptionDao;

    // 보기 등록
    @Override
    public int insert(QuestionOptionDto questionOptionDto) {

        // 보기 번호 생성
        int optionNo = questionOptionDao.sequence();
        
        questionOptionDto.setOptionNo(optionNo);
        
        // 보기 등록
        questionOptionDao.insert(questionOptionDto);

        return optionNo;
    }

    // 보기 단일 조회
    @Override
    public QuestionOptionDto selectOne(int optionNo) {

        QuestionOptionDto option = questionOptionDao.selectOne(optionNo);

        if (option == null) {
            throw new TargetNotfoundException();
        }

        return option;
    }

    // 특정 문제의 보기 목록 조회
    @Override
    public List<QuestionOptionDto> selectListByQuestion(int questionNo) {
    
        return questionOptionDao.selectListByQuestion(questionNo);
    }

    // 보기 수정
    @Override
    public boolean update(QuestionOptionDto questionOptionDto) {

        // 보기 존재 확인
        QuestionOptionDto findOption = questionOptionDao.selectOne(questionOptionDto.getOptionNo());

        if (findOption == null) {
            throw new TargetNotfoundException();
        }

        return questionOptionDao.update(questionOptionDto);
    }

    // 보기 삭제
    @Override
    public boolean delete(int optionNo) {

        // 보기 존재 확인
        QuestionOptionDto option = questionOptionDao.selectOne(optionNo);

        if (option == null) {
            throw new TargetNotfoundException();
        }
        
        return questionOptionDao.delete(optionNo);
    }

    // 특정 문제의 보기 전체 삭제
    @Override
    public boolean deleteByQuestion(int questionNo) {
        return questionOptionDao.deleteByQuestion(questionNo);
    }
}