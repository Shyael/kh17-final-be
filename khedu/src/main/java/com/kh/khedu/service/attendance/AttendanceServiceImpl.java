package com.kh.khedu.service.attendance;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.AttendanceDao;
import com.kh.khedu.dao.ClassSessionDao;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dto.AttendanceDto;
import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.vo.attendance.AttendanceStudentResponseVO;
import com.kh.khedu.vo.attendance.AttendanceUpdateByAdminVO;
import com.kh.khedu.vo.attendance.SessionAttendanceDetailVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private AttendanceDao attendanceDao;
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private ClassSessionDao classSessionDao;
	
	// 허용되는 상태 제약조건 목록
    private static final List<String> VALID_ATTENDANCE_STATES = 
    		Arrays.asList("미출결", "출석", "결석", "지각", "조퇴");
	
	@Override
	public void updateAttendanceByAdmin(AttendanceUpdateByAdminVO request, TokenParseResponseVO parseVO) {
		// [1] 직원 계정인지 1차 검증
        if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
            throw new WhoAreYouException("직원 전용 기능입니다");
        }
        // [2] 상태 제약조건 검증
        if (!VALID_ATTENDANCE_STATES.contains(request.getAttendanceState())) {
            throw new IllegalArgumentException("유효하지 않은 출결 상태값입니다. ('미출결', '출석', '결석', '지각', '조퇴' 중 입력)");
        }
        
        // [3] 출결 데이터 존재 여부 확인
        AttendanceDto attendance = attendanceDao.selectOneByAttendanceNo(request.getAttendanceNo());
    	if(attendance == null) {
    		throw new TargetNotfoundException("해당 출결 정보를 찾을 수 없습니다");
    	}
        
        
        //[4] 강사(TUTOR) 권한일 경우 본인 수업의 수강생인지 대조 (ADMIN, DESK는 프리패스)
        List<String> roles = parseVO.getRoleNames();
        boolean isManager = roles != null && (roles.contains("ADMIN") || roles.contains("DESK"));
        
        if(!isManager && roles != null && roles.contains("TUTOR")) {
        	Integer tutorNo = attendanceDao.selectTutorNoByAttendanceNo(request.getAttendanceNo());
        	if(tutorNo == null || !tutorNo.equals(parseVO.getNoType())) {
        		throw new WhoAreYouException("본인이 담당하는 수업 세션의 출결만 수정할 수 있습니다.");
        	}
        }else if (!isManager) {
        	throw new WhoAreYouException("출결을 수정할 수 있는 권한이 없습니다");
        }
        
        //[5] 출결 상태 정정 갱신
        attendanceDao.updateAttendanceStateByAdmin(request.getAttendanceNo(), request.getAttendanceState());
	}

	@Override
	public SessionAttendanceDetailVO getSessionAttendanceDetail(int sessionNo, TokenParseResponseVO parseVO) {
		// [1] 직원 권한 1차 검증
        if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
            throw new WhoAreYouException("직원 전용 기능입니다");
        }

        // [2] 세션 존재 여부 확인
        ClassSessionDto session = classSessionDao.selectOneBySessionNo(sessionNo);
        if (session == null) {
            throw new TargetNotfoundException("해당 세션이 존재하지 않습니다");
        }

        // [3] TUTOR 권한일 경우 본인 담당 강좌의 세션인지 대조 (ADMIN, DESK는 프리패스)
        List<String> roles = parseVO.getRoleNames();
        boolean isManager = roles != null && (roles.contains("ADMIN") || roles.contains("DESK"));
        
        if (!isManager && roles != null && roles.contains("TUTOR")) {
            CourseDto courseDto = courseDao.selectOneByScheduleNo(session.getScheduleNo());
            if (courseDto == null || courseDto.getEmployeeNo() != parseVO.getNoType()) {
                throw new WhoAreYouException("본인이 담당하는 수업 세션의 출결만 조회할 수 있습니다");
            }
        } else if (!isManager) {
            throw new WhoAreYouException("출결을 조회할 수 있는 권한이 없습니다");
        }
        
        //[4] 출석부 목록 조회
        List<AttendanceStudentResponseVO> studentList = attendanceDao.selectAttendanceListBySessionNo(sessionNo);
        
        //[5] 인원 통계 산출
        int presentCount = 0;
        int lateCount = 0;
        int earlyLeaveCount = 0;
        int absentCount = 0;
        int uncheckedCount = 0;
        
        for(AttendanceStudentResponseVO student : studentList) {
	        	switch (student.getAttendanceState()) {
	            case "출석": presentCount++; break;
	            case "지각": lateCount++; break;
	            case "조퇴": earlyLeaveCount++; break;
	            case "결석": absentCount++; break;
	            case "미출결": uncheckedCount++; break;
        	}
        }
        
        // [6] 최종 결과 DTO 빌드 후 반환
        return SessionAttendanceDetailVO.builder()
                .sessionNo(sessionNo)
                .totalCount(studentList.size())
                .presentCount(presentCount)
                .lateCount(lateCount)
                .earlyLeaveCount(earlyLeaveCount)
                .absentCount(absentCount)
                .uncheckedCount(uncheckedCount)
                .studentList(studentList)
                .build();
	}

}
