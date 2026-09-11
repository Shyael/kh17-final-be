package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.vo.room.RoomDetailResponseVO;
import com.kh.khedu.vo.room.RoomListVO;
import com.kh.khedu.vo.room.RoomUserVO;

public interface RoomDao {
	int sequence();
	void insert(RoomDto roomDto);
	
	RoomDto selectOne(int roomNo);
	RoomDto selectOneForCheck(int accountNo);
	
	List<RoomListVO> selectList();
	List<RoomListVO> selectList(int accountNo);
	
	List<Integer> getMembers(int roomNo);
	List<RoomUserVO> getMemberInfo(int roomNo);
}
