package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;

@Repository
public class ClassroomDaoMybatis implements ClassroomDao {

	@Autowired
	private SqlSession sqlSession;
	
	// 최초 등록화면 진입 시 강의실 조회
	@Override
	public List<ClassroomWhenRegisterVO> classroomListWhenRegister() {
		return sqlSession.selectList("mapper.classroom.classroomListWhenRegister");
	}
	
	// 필터링 된 강의실 조회
	@Override
	public List<ClassroomWhenRegisterVO> selectAvailableClassroomList(AvailableClassroomRequestVO request) {
		return sqlSession.selectList("mapper.classroom.selectAvailableClassroomList", request);
	}
	
	//강의실 사용가능여부
	@Override
	public int checkClassroom(int classroomNo, int courseLimit) {
		Map<String, Object> param = new HashMap<>();
		param.put("classroomNo", classroomNo);
		param.put("courseLimit", courseLimit);
		
		return sqlSession.selectOne("mapper.classroom.checkClassroom", param);
	}

}
