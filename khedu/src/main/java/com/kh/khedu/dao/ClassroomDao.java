package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;

public interface ClassroomDao {
	
	//최초 화면 진입 시 전체 후보 조회
	List<ClassroomWhenRegisterVO> classroomListWhenRegister();
	
	//기준(수강인원 + 가능시간)에 부합한 사용 가능한 강의실 조회
	List<ClassroomWhenRegisterVO> selectAvailableClassroomList(
				AvailableClassroomRequestVO request
	);
	
	//강의실 사용 가능 여부 확인
	int checkClassroom(int classroomNo, int courseLimit);
	
}
