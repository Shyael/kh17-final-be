package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.vo.room.RoomListVO;
import com.kh.khedu.vo.room.RoomUserVO;

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
	public RoomDto selectOne(int roomNo) {
		return sqlSession.selectOne("mapper.room.find", roomNo);
	}
	@Override
	public RoomDto selectOneForCheck(int accountNo) {
		return sqlSession.selectOne("mapper.room.check", accountNo);
	}
	@Override
	public List<RoomListVO> selectList() {
		return sqlSession.selectList("mapper.room.list");
	}
	@Override
	public List<RoomListVO> selectList(int accountNo) {
		return sqlSession.selectList("mapper.room.listUpgrade", accountNo);
	}
	@Override
	public List<Integer> getMembers(int roomNo) {
		return sqlSession.selectList("mapper.room.member", roomNo);
	}
	@Override
	public List<RoomUserVO> getMemberInfo(int roomNo) {
		return sqlSession.selectList("mapper.room.memberInfo", roomNo);
	}
}
