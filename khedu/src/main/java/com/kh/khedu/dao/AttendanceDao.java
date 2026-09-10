package com.kh.khedu.dao;

import java.sql.Timestamp;

import com.kh.khedu.dto.AttendanceDto;

public interface AttendanceDao {

	AttendanceDto selectBySessionAndStudent(int sessionNo, int studentNo);

	boolean updateAttendanceState(int attendanceNo, String attendanceState, Timestamp timestamp);

}
