package com.kh.khedu.service.attendance;

import com.kh.khedu.vo.attendance.AttendanceUpdateByAdminVO;
import com.kh.khedu.vo.attendance.SessionAttendanceDetailVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface AttendanceService {
	//관리자의 출결 수정
	void updateAttendanceByAdmin(AttendanceUpdateByAdminVO request, TokenParseResponseVO parseVO);

	//관리자 : 세션의 출결학생 조회
	SessionAttendanceDetailVO getSessionAttendanceDetail(int sessionNo, TokenParseResponseVO parseVO);
	
}
