package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.vo.room.RoomListVO;

@Repository
public class RoomDaoMybatis implements RoomDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.room.sequence");
	}
	@Override
	public void insert(RoomDto roomDto) {
		sqlSession.insert("mapper.room.create", roomDto);
	}
	@Override
	public List<RoomListVO> selectList() {
		return sqlSession.selectList("mapper.room.list");
	}
	@Override
	public List<RoomListVO> selectList(int accountNo) {
		return sqlSession.selectList("mapper.room.listUpgrade", accountNo);
	}
}
