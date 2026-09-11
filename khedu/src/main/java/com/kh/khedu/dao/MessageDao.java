package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.vo.message.MessageVO;
import com.kh.khedu.vo.room.RoomChatMessageVO;
import com.kh.khedu.vo.room.RoomMessageRequestVO;
import com.kh.khedu.vo.room.RoomSystemMessageVO;

public interface MessageDao {
	int sequence();
	void insertChat(RoomChatMessageVO message);
	void insertSystem(RoomSystemMessageVO message);
	
	List<MessageVO> selectList(int messageRoom);
	List<MessageVO> selectList(int messageRoom, int size);
	List<MessageVO> selectList(int messageRoom, int size, int lastMessageNo);
	
	List<MessageVO> selectList(int roomNo, RoomMessageRequestVO request);
	int count(int roomNo, RoomMessageRequestVO request);
}
