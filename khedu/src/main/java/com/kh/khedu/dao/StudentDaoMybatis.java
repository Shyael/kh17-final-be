package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.StudentDto;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;
import com.kh.khedu.vo.payment.StudentDiscountVO;
import com.kh.khedu.vo.student.StudentDetailResponseVO;
import com.kh.khedu.vo.student.StudentListResponseVO;
import com.kh.khedu.vo.student.StudentUpdateRequestVO;
import com.kh.khedu.vo.student.StudentVO;


@Repository
public class StudentDaoMybatis implements StudentDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.student.sequence");
	}

	@Override
	public void insert(StudentVO studentVO) {
		sqlSession.insert("mapper.student.join", studentVO);
	}

    
    // 학생 목록 전체 조회
	@Override
    public List<StudentListResponseVO> selectList() {
        // sqlSession에게 "student"라는 namespace의 "list"라는 쿼리를 실행하라고 지시
        return sqlSession.selectList("mapper.student.list");
    }
	
	//학생 목록 상세 조회
	@Override
	public StudentDetailResponseVO selectDetail(int studentNo) {
	    // mapper.student.detail 쿼리를 실행하면서 studentNo 값을 같이 넘겨줍니다.
	    return sqlSession.selectOne("mapper.student.detail", studentNo);
	}
	
	@Override
	public void updateAccount(StudentUpdateRequestVO requestVO) {
	    sqlSession.update("mapper.student.updateAccountInfo", requestVO);
	}

	@Override
	public void updateStudent(StudentUpdateRequestVO requestVO) {
	    sqlSession.update("mapper.student.updateStudentInfo", requestVO);
	}
	
	@Override
    public List<StudentDiscountVO> selectStudentDiscounts(int studentNo) {
        return sqlSession.selectList("mapper.discount.selectStudentDiscounts", studentNo);
    }

    @Override
    public void insertStudentDiscount(StudentDiscountVO studentDiscountVO) {
        sqlSession.insert("mapper.discount.insertStudentDiscount", studentDiscountVO);
    }

    @Override
    public void deleteStudentDiscount(int studentDiscountNo) {
        sqlSession.delete("mapper.discount.deleteStudentDiscount", studentDiscountNo);
    }
	@Override
	public StudentDto selectOne(int accountNo) {
		return sqlSession.selectOne("mapper.student.findByAccountNo", accountNo);
	}

	@Override
	public boolean updateAll(StudentDto studentDto) {
		return sqlSession.update("mapper.student.updateAll", studentDto) > 0;
	}

	@Override
	public ParentStudentVO selectOneRelationByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.student.findParentStudentByAccountNo", accountNo);
	}
}
