package com.kh.khedu.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.vo.message.MessageVO;
import com.kh.khedu.vo.room.RoomChatMessageVO;
import com.kh.khedu.vo.room.RoomMessageRequestVO;
import com.kh.khedu.vo.room.RoomSystemMessageVO;

@Repository
public class MessageDaoMybatis implements MessageDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("mapper.message.sequence");
	}
	@Transactional
	@Override
	public void insertChat(RoomChatMessageVO message) {
		sqlSession.insert("mapper.message.add", message);
		sqlSession.insert("mapper.message.addChat", message);
	}
	@Transactional
	@Override
	public void insertSystem(RoomSystemMessageVO message) {
		sqlSession.insert("mapper.message.add", message);
		sqlSession.insert("mapper.message.addSystem", message);
	}
	
	@Override
	public List<MessageVO> selectList(int messageRoom) {
		//return sqlSession.selectList("mapper.message.selectTest", messageRoom);
		Map<String, Object> params = new HashMap<>();
		params.put("messageRoom", messageRoom);
		return sqlSession.selectList("mapper.message.selectMessages", params);
	}
	@Override
	public List<MessageVO> selectList(int messageRoom, int size) {
		Map<String, Object> params = new HashMap<>();
		params.put("messageRoom", messageRoom);
		params.put("size", size);
		return sqlSession.selectList("mapper.message.selectMessages", params);
	}
	@Override
	public List<MessageVO> selectList(int messageRoom, int size, int lastMessageNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("messageRoom", messageRoom);
		params.put("size", size);
		params.put("lastMessageNo", lastMessageNo);
		return sqlSession.selectList("mapper.message.selectMessages", params);
	}
	
	@Override
	public List<MessageVO> selectList(int messageRoom, RoomMessageRequestVO request) {
		Map<String, Object> params = new HashMap<>();
		params.put("messageRoom", messageRoom);
		params.put("size", request.getSize());
		params.put("lastMessageNo", request.getLastMessageNo());
		return sqlSession.selectList("mapper.message.selectMessages", params);
	}
	@Override
	public int count(int messageRoom, RoomMessageRequestVO request) {
		Map<String, Object> params = new HashMap<>();
		params.put("messageRoom", messageRoom);
		params.put("lastMessageNo", request.getLastMessageNo());
		return sqlSession.selectOne("mapper.message.countMessages", params);
	}
}
