package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
