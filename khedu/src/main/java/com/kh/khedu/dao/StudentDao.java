package com.kh.khedu.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.StudentDetailResponseVO;
import com.kh.khedu.vo.student.StudentListResponseVO;
import com.kh.khedu.vo.student.StudentUpdateRequestVO;
import com.kh.khedu.vo.student.StudentVO;


@Repository
public interface StudentDao {

	int sequence(); //등록
	void insert(StudentVO studentVO);
	List<StudentListResponseVO> selectList();//학생목록
	StudentDetailResponseVO selectDetail(int studentNo);//학생 상세정보
	void updateAccount(StudentUpdateRequestVO requestVO);
	void updateStudent(StudentUpdateRequestVO requestVO);
	//학생 개인 할인 적용
    List<StudentDiscountVO> selectStudentDiscounts(int studentNo);
    void insertStudentDiscount(StudentDiscountVO studentDiscountVO);
    void deleteStudentDiscount(int studentDiscountNo);
	StudentDto selectOne(int accountNo);
	boolean updateAll(StudentDto studentDto);
}
