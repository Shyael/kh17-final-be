package com.kh.khedu.service.attendance;



import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToNormalRequestVO;

import com.kh.khedu.vo.payroll.request.AttendanceLeaveRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToNormalRequestVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockInResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockOutResponseVO;


public interface EmployeeAttendanceService {
	
	boolean working(
	        TokenParseResponseVO parseVO);
	
	
	AttendanceClockInResponseVO clockIn(
           
            TokenParseResponseVO parseVO);

    AttendanceClockOutResponseVO clockOut(
            TokenParseResponseVO parseVO);


    void absent(
            AttendanceAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO);

    void paidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO);

    void unpaidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO);


    void normalToNormal(
            AttendanceNormalToNormalRequestVO requestVO,
            TokenParseResponseVO parseVO);

    void normalToAbsent(
            AttendanceNormalToAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO);

    void absentToNormal(
            AttendanceAbsentToNormalRequestVO requestVO,
            TokenParseResponseVO parseVO);

    void absentToAbsent(
            AttendanceAbsentToAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO);
    
    void autoAbsent();
}
