package com.kh.khedu.dao;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.AttendanceDto;
import com.kh.khedu.vo.attendance.AttendanceStudentResponseVO;

public interface AttendanceDao {

	AttendanceDto selectBySessionAndStudent(int sessionNo, int studentNo);

	boolean updateAttendanceState(int attendanceNo, String attendanceState, Timestamp timestamp);
	
	int updateAbsentForUncheckedStudents(int sessionNo);
	
	//누락된 출석부가 있을 경우 
	void initAttendance(int sessionNo, int courseNo);
	
	//출결번호로 조회
	AttendanceDto selectOneByAttendanceNo(int attendanceNo);
	//출결번호로 담당 강사 번호 출력
	Integer selectTutorNoByAttendanceNo(int attendanceNo);
	//관리자/담당 강사용 수동 출결상태 정정
	boolean updateAttendanceStateByAdmin(int attendanceNo, String attendanceState);
	//세션의 출석 학생들 목록
    List<AttendanceStudentResponseVO> selectAttendanceListBySessionNo(int sessionNo);
}
