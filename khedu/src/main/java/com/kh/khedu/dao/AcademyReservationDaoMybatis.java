package com.kh.khedu.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.consult.ConsultReservationInsertRequestVO;

@Repository
public class AcademyReservationDaoMybatis implements AcademyReservationDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.academyReservation.sequence");
	}
	
	@Override
	public boolean insertReservation(ConsultReservationInsertRequestVO request) {
		return sqlSession.insert("mapper.academyReservation.insertReservation", request) > 0;
	}
}
