package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.RoomDto;
import com.kh.khedu.vo.room.RoomListVO;

public interface RoomDao {
	int sequence();
	void insert(RoomDto roomDto);
	
	List<RoomListVO> selectList();
	List<RoomListVO> selectList(int accountNo);
}
