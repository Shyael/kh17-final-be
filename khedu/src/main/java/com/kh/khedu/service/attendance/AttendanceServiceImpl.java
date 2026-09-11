package com.kh.khedu.service.attendance;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.kh.khedu.vo.attendance.KioskAttendanceRequestVO;
import com.kh.khedu.vo.attendance.KioskAttendanceResponseVO;
import com.kh.khedu.vo.attendance.KioskStudentCandidateVO;
import com.kh.khedu.vo.attendance.KioskTargetSessionVO;
import com.kh.khedu.vo.attendance.SessionAttendanceDetailVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
    // 수업 시작 후 10분 이내: 출석, 10분 초과: 지각
    private static final int LATE_THRESHOLD_MINUTES = 10;
    
    
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

	/*================================================
	 * 키오스크 학생 출결 관련 서비스
	 * ==================================================*/
	@Override
	@Transactional
	public KioskAttendanceResponseVO processKioskAttendance(KioskAttendanceRequestVO request) {
		int studentNo;
		String studentName;
		
		//[1] 학생 식별
        if (request.getSelectedStudentNo() != null) {
            // 중복 모달에서 학생 본인 이름을 선택한 경우
            KioskStudentCandidateVO student = attendanceDao.selectStudentInfoByNo(request.getSelectedStudentNo());
            if (student == null) {
                throw new TargetNotfoundException("선택한 학생 정보를 찾을 수 없습니다.");
            }
            studentNo = student.getStudentNo();
            studentName = student.getStudentName();
        } else {
            // 키패드로 입력한 휴대폰 뒷자리 4자리로 조회
            String phoneTail = request.getPhoneTail();
            if (phoneTail == null || phoneTail.length() != 4) {
                throw new IllegalArgumentException("휴대폰 번호 뒷 4자리를 입력해 주세요.");
            }

            List<KioskStudentCandidateVO> candidates = attendanceDao.selectStudentsByPhoneTail(phoneTail);
            if (candidates == null || candidates.isEmpty()) {
                throw new TargetNotfoundException("등록되지 않은 휴대폰 번호입니다.");
            }

            // 번호 뒷자리가 우연히 겹친 학생이 2명 이상인 경우 -> 화면에 모달 표출 요청
            if (candidates.size() > 1) {
                return KioskAttendanceResponseVO.builder()
                        .actionType("MULTIPLE_CANDIDATES")
                        .candidateList(candidates)
                        .message("동일한 번호의 학생이 있습니다. 본인의 이름을 선택해 주세요.")
                        .build();
            }

            studentNo = candidates.get(0).getStudentNo();
            studentName = candidates.get(0).getStudentName();
        }
        
     // [2] 현재 시각 기준 태그 가능한 수업 세션 확인
        KioskTargetSessionVO target = attendanceDao.selectCurrentTargetAttendance(studentNo);
        if (target == null) {
            throw new TargetNotfoundException(studentName + " 학생, 현재 출석 가능한 수업이 없습니다. (수업 시작 30분 전부터 태그 가능)");
        }

        // [3] 중복 태그 방어 (이미 출석/지각 처리된 세션)
        if (!AttendanceDto.STATE_UNCHECKED.equals(target.getAttendanceState())) {
            return KioskAttendanceResponseVO.builder()
                    .actionType("SUCCESS")
                    .studentName(target.getStudentName())
                    .courseTitle(target.getCourseTitle())
                    .attendanceState(target.getAttendanceState())
                    .message(target.getStudentName() + " 학생은 이미 [" + target.getCourseTitle() + "] 수업에 " + target.getAttendanceState() + " 처리되었습니다.")
                    .build();
        }
		
     // [4] 출석 / 지각 판정 (수업 시작 + 10분 기준)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStartTime = target.getSessionStart();
        LocalDateTime lateThreshold = classStartTime.plusMinutes(LATE_THRESHOLD_MINUTES);

        String determinedState = now.isAfter(lateThreshold)
                ? AttendanceDto.STATE_LATE 
                : AttendanceDto.STATE_PRESENT;

        // [5] 출결 테이블 갱신 (attendance_state = '미출결' 조건으로 동시성 제어)
        int updatedRows = attendanceDao.updateKioskAttendance(target.getAttendanceNo(), determinedState);
        if (updatedRows == 0) {
            throw new TargetNotfoundException("출석 처리 중 충돌이 발생했습니다. 다시 시도해 주세요.");
        }

        String tagTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info("[키오스크 출결 완료] 학생: {}(#{}), 강좌: {}, 상태: {}, 태그시각: {}", 
                studentName, studentNo, target.getCourseTitle(), determinedState, tagTime);

        return KioskAttendanceResponseVO.builder()
                .actionType("SUCCESS")
                .studentName(studentName)
                .courseTitle(target.getCourseTitle())
                .attendanceState(determinedState)
                .attendanceTime(tagTime)
                .message(studentName + " 학생, [" + target.getCourseTitle() + "] 수업에 " + determinedState + " 처리되었습니다.")
                .build();
	}
	
	
	

}
