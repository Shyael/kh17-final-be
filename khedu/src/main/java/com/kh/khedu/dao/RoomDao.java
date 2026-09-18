package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.dto.RoomUserDto;
import com.kh.khedu.vo.room.RoomListVO;
import com.kh.khedu.vo.room.RoomUserVO;
import com.kh.khedu.vo.room.RoomVO;

public interface RoomDao {
	int roomSequence();
	int roomUserSequence();
	void insertRoom(RoomDto roomDto);
	void insertRoomUser(RoomUserDto roomUserDto);
	
	RoomVO selectOne(int roomNo);
	RoomVO selectOneForCheck(int accountNo);
	RoomVO selectOneForPrivateCheck(int myAccountNo, int accountNo);
	
	List<RoomListVO> selectMyList(int accountNo);
	List<RoomListVO> selectConsultList(int accountNo);
	List<RoomListVO> selectCourseList(int accountNo);
	List<RoomListVO> selectMyTutorList(int accountNo);
	
	List<Integer> getMembers(int roomNo);
	List<RoomUserVO> getMemberInfo(int roomNo);
	
	void readRoomUser(int roomNo, int accountNo);
	void readRoomEmployee(int roomNo);
	
	void enter(int sequence, int roomNo, int accountNo);
	void leave(int roomNo, int accountNo);
}
