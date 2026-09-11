package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.dto.RoomUserDto;
import com.kh.khedu.vo.room.RoomListVO;
import com.kh.khedu.vo.room.RoomUserVO;
import com.kh.khedu.vo.room.RoomVO;

@Repository
public class RoomDaoMybatis implements RoomDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int roomSequence() {
		return sqlSession.selectOne("mapper.room.roomSequence");
	}
	@Override
	public int roomUserSequence() {
		return sqlSession.selectOne("mapper.room.roomUserSequence");
	}
	@Override
	public void insertRoom(RoomDto roomDto) {
		sqlSession.insert("mapper.room.create", roomDto);
	}
	@Override
	public void insertRoomUser(RoomUserDto roomUserDto) {
		sqlSession.insert("mapper.room.createRoomUser", roomUserDto);
	}
	@Override
	public RoomVO selectOne(int roomNo) {
		return sqlSession.selectOne("mapper.room.find", roomNo);
	}
	@Override
	public RoomVO selectOneForCheck(int accountNo) {
		return sqlSession.selectOne("mapper.room.check", accountNo);
	}
	@Override
	public List<RoomListVO> selectMyList(int accountNo) {
		return sqlSession.selectList("mapper.room.myList");
	}
	@Override
	public List<RoomListVO> selectConsultList(int accountNo) {
		return sqlSession.selectList("mapper.room.consultList", accountNo);
	}
	@Override
	public List<Integer> getMembers(int roomNo) {
		return sqlSession.selectList("mapper.room.member", roomNo);
	}
	@Override
	public List<RoomUserVO> getMemberInfo(int roomNo) {
		return sqlSession.selectList("mapper.room.memberInfo", roomNo);
	}
	@Override
	public void readRoomUser(int roomNo, int accountNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("roomNo", roomNo);
		params.put("accountNo", accountNo);
		sqlSession.update("mapper.room.readRoomUser", params);
	}
	@Override
	public void readRoomEmployee(int roomNo) {
		sqlSession.update("mapper.room.readRoomEmployee", roomNo);
	}
}
