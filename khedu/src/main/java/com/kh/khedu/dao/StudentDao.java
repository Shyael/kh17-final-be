package com.kh.khedu.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.util.PaginationVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.StudentDetailResponseVO;
import com.kh.khedu.vo.student.StudentListResponseVO;
import com.kh.khedu.vo.student.StudentUpdateRequestVO;
import com.kh.khedu.vo.student.StudentVO;


@Repository
public interface StudentDao {

	int sequence(); //등록
	void insert(StudentVO studentVO);
	// 전체 학생 수 조회 (페이징 계산용)
    int count(String filter, String searchKeyword);
    // 페이징이 적용된 학생 목록 조회
    List<StudentListResponseVO> list(String filter, String searchKeyword, PaginationVO paginationVO);
	StudentDetailResponseVO selectDetail(int studentNo);//학생 상세정보
	void updateAccount(StudentUpdateRequestVO requestVO);
	void updateStudent(StudentUpdateRequestVO requestVO);
	//학생 개인 할인 적용
    List<StudentDiscountVO> selectStudentDiscounts(int studentNo);
    void insertStudentDiscount(StudentDiscountVO studentDiscountVO);
    void deleteStudentDiscount(int studentDiscountNo);
	StudentDto selectOne(int accountNo);
	boolean updateAll(StudentDto studentDto);
	boolean approveStudent(int studentNo);
	// 학년 조회 메서드 추가
    String selectStudentGrade(int studentNo);
}
