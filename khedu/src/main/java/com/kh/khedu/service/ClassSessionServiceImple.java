package com.kh.khedu.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AttendanceDao;
import com.kh.khedu.dao.ClassSessionDao;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dao.ScheduleDao;
import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.vo.classSession.AdminClassSessionInsertVO;
import com.kh.khedu.vo.classSession.AdminClassSessionStatusVO;
import com.kh.khedu.vo.classSession.ClassSessionEndRequestVO;
import com.kh.khedu.vo.classSession.ClassSessionStartRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public class ClassSessionServiceImple implements ClassSessionService {

	@Autowired
	private ScheduleDao scheduleDao;
	@Autowired
	private ClassSessionDao classSessionDao;
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private AttendanceDao attendanceDao;
	
	// classSession 시작
	@Override
	@Transactional
	public void StartClass(ClassSessionStartRequestVO request, TokenParseResponseVO parseVO) {
		
		// [0] 직원 계정 1차 검증
		if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
			throw new WhoAreYouException("직원 전용 기능입니다");
		}
		
		// [1] scheduleNo로 Schedule 조회
		int scheduleNo = request.getScheduleNo();
		ScheduleDto scheduleDto = scheduleDao.selectOneByScheduleNo(scheduleNo);
		
		// [2] Schedule 존재 여부 확인
		if(scheduleDto == null) {
			throw new TargetNotfoundException("해당 스케줄이 존재하지 않습니다");
		}
		// [2-1] 강사(TUTOR) 권한 사용자인 경우 본인 강좌 스케줄인지 대조 (ADMIN/DESK는 프리패스)
		if (parseVO.getRoleNames() != null && parseVO.getRoleNames().contains("TUTOR")) {
			CourseDto courseDto = courseDao.selectOneByScheduleNo(scheduleNo);
			if (courseDto == null || courseDto.getEmployeeNo() != parseVO.getNoType()) {
				throw new WhoAreYouException("본인이 담당하는 강좌의 수업만 시작할 수 있습니다");
			}
		}
		
		// [3] 오늘이 수업 요일인지 확인
		String todayKorean = LocalDate.now().getDayOfWeek()
				.getDisplayName(TextStyle.NARROW, Locale.KOREAN);
		String todayInput = scheduleDto.getScheduleWeek(); 
		boolean checkWeek = todayKorean.equals(todayInput);
		if(!checkWeek) {
			throw new TargetNotfoundException("오늘은 수업 요일이 아닙니다");
		}
				
		// [4] scheduleStart / scheduleEnd 조합 (Timestamp 생성)
		LocalDate today = LocalDate.now();
		LocalTime localStartTime = LocalTime.parse(scheduleDto.getScheduleStart());
		LocalTime localEndTime = LocalTime.parse(scheduleDto.getScheduleEnd());
		
		Timestamp sessionStart = Timestamp.valueOf(today.atTime(localStartTime));
		Timestamp sessionEnd = Timestamp.valueOf(today.atTime(localEndTime));
		
		// [5] 오늘의 classSession 중복 확인
		ClassSessionDto findClassSessionDto = classSessionDao.selectTodaySession(scheduleNo, sessionStart);	
		if(findClassSessionDto != null) {
			throw new TargetNotfoundException("해당 수업 세션이 이미 존재합니다");
		}
		
		// [6] ClassSessionDto 생성 (DB DEFAULT 값으로 '진행중' 처리)
		int sessionNo = classSessionDao.sequence();
		ClassSessionDto classSessionDto = ClassSessionDto.builder()
					.sessionNo(sessionNo)
					.scheduleNo(scheduleNo)
					.classroomNo(scheduleDto.getClassroomNo())
					.sessionStart(sessionStart)
					.sessionEnd(sessionEnd)
				.build();
		
		// [7] ClassSession 등록
		classSessionDao.insert(classSessionDto);
		
		// [7-1] 세션 생성 즉시 수강생 출석부 '미출결' 생성
		attendanceDao.initAttendance(sessionNo, scheduleDto.getCourseNo());
		
		// [8] 첫 수업 여부 확인 (개강일 등록)
		if(scheduleDto.getScheduleOpen() == null) {
			scheduleDao.updateOpen(scheduleNo, today);
		}
			
		// [9] Course 상태를 진행중으로 변경
		CourseDto courseDto = courseDao.selectOneByCourseNo(scheduleDto.getCourseNo());
		if(courseDto != null && CourseDto.STATUS_RECRUITING.equals(courseDto.getCourseStatus())) {
			courseDao.updateStatus(scheduleDto.getCourseNo(), CourseDto.STATUS_RUNNING);
		}	
	}
	
	// classSession 종료
	@Override
	@Transactional
	public String EndClass(ClassSessionEndRequestVO request, TokenParseResponseVO parseVO) {
		// [0] 직원 계정 1차 검증
		if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
			throw new WhoAreYouException("직원 전용 기능입니다");
		}
		
		// [1] sessionNo로 해당 ClassSession 조회
		int sessionNo = request.getSessionNo();
		ClassSessionDto classSessionDto = classSessionDao.selectOneBySessionNo(sessionNo);
		if(classSessionDto == null) {
			throw new TargetNotfoundException("해당 수업 세션이 존재하지 않습니다");
		}
		// [1-1] 강사(TUTOR) 권한 사용자인 경우 본인 세션인지 대조 (ADMIN/DESK는 프리패스)
		if (parseVO.getRoleNames() != null && parseVO.getRoleNames().contains("TUTOR")) {
			CourseDto courseDto = courseDao.selectOneByScheduleNo(classSessionDto.getScheduleNo());
			if (courseDto == null || courseDto.getEmployeeNo() != parseVO.getNoType()) {
				throw new WhoAreYouException("본인이 담당하는 수업 세션만 종료할 수 있습니다");
			}
		}
		
		// [2] 강좌가 진행중인지 확인
		if(!ClassSessionDto.STATUS_RUNNING.equals(classSessionDto.getSessionStatus())) {
			throw new WhoAreYouException("현재 진행중인 수업 세션이 아닙니다(이미 종료되었거나 취소됨)");
		}
		
		// [2-1] 수업 종료 시간 도달 여부 검증 (종료 시각 이전 종료 시도 차단)
		Timestamp sessionEndTimestamp = classSessionDto.getSessionEnd();
		if (sessionEndTimestamp != null) {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime sessionEndTime = sessionEndTimestamp.toLocalDateTime();
			
			// 현재 시각이 수업 종료 시각 이전인 경우 예외 발생
			if (now.isBefore(sessionEndTime)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
				String formattedEndTime = sessionEndTime.format(formatter);
				throw new IllegalArgumentException("수업 종료 시간(" + formattedEndTime + ") 이전에는 수업을 종료할 수 없습니다.");
			}
		}
		
		// [3] ClassSession 상태를 '종료'로 변경 및 종료시각 갱신
		classSessionDao.updateSessionToEnd(sessionNo, ClassSessionDto.STATUS_CLOSED);
		
		// [4] 연관된 Schedule 정보 조회
		ScheduleDto scheduleDto = scheduleDao.selectOneByScheduleNo(classSessionDto.getScheduleNo());
		if(scheduleDto == null) {
			throw new TargetNotfoundException("연관된 스케줄 정보가 존재하지 않습니다");
		}
		
		// [5] 연관된 Course 정보 조회 및 상태 검증
		CourseDto courseDto = courseDao.selectOneByCourseNo(scheduleDto.getCourseNo());
		if(courseDto == null) {
			throw new TargetNotfoundException("연관된 강좌 정보가 존재하지 않습니다");
		}
		
		// [6] 종강일 도달 여부 확인 후 Course 종강 처리
		LocalDate scheduleClose = scheduleDto.getScheduleClose();
		if(scheduleClose != null) {
			LocalDate today = LocalDate.now();
			if(today.isEqual(scheduleClose) || today.isAfter(scheduleClose)) {
				if(CourseDto.STATUS_RUNNING.equals(courseDto.getCourseStatus())) {
					courseDao.updateStatus(scheduleDto.getCourseNo(), CourseDto.STATUS_CLOSED);
				}
			}
		}
		
		// [7] 미출결 수강생 일괄 결석 처리
		int absentCount = attendanceDao.updateAbsentForUncheckedStudents(sessionNo);
		return "수업 세션이 성공적으로 종료되었습니다. (결석 처리: " + absentCount + "명)";
	}

	/*
	 * 관리자용 수동 관리 로직 
	 */
	
	// [관리자 1] 강사가 [수업 시작]을 안 누르고 지나갔을 때 사후 수동 등록
	@Override
	@Transactional
	public void insertSessionByAdmin(AdminClassSessionInsertVO request, TokenParseResponseVO parseVO) {
		// [1] 직원 계정인지 1차 차단
		if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
			throw new WhoAreYouException("직원 전용 기능입니다");
		}
		
		ScheduleDto scheduleDto = scheduleDao.selectOneByScheduleNo(request.getScheduleNo());
		if(scheduleDto == null) {
			throw new TargetNotfoundException("해당 스케줄이 존재하지 않습니다");
		}

		// [2] 강사(TUTOR) 권한을 가진 사용자인 경우 본인 수업인지 대조
		if (parseVO.getRoleNames() != null && parseVO.getRoleNames().contains("TUTOR")) {
			CourseDto courseDto = courseDao.selectOneByScheduleNo(request.getScheduleNo());
			if (courseDto == null || courseDto.getEmployeeNo() != parseVO.getNoType()) {
				throw new WhoAreYouException("본인이 담당하는 강좌의 스케줄만 등록할 수 있습니다");
			}
		}
		
		LocalTime startTime = LocalTime.parse(scheduleDto.getScheduleStart());
		LocalTime endTime = LocalTime.parse(scheduleDto.getScheduleEnd());
		Timestamp sessionStart = Timestamp.valueOf(request.getTargetDate().atTime(startTime));
		Timestamp sessionEnd = Timestamp.valueOf(request.getTargetDate().atTime(endTime));
		
		// 중복 체크
		ClassSessionDto exist = classSessionDao.selectTodaySession(request.getScheduleNo(), sessionStart);
		if (exist != null) {
			throw new TargetNotfoundException("해당 날짜에 이미 세션이 존재합니다");
		}
		
		int sessionNo = classSessionDao.sequence();
		ClassSessionDto sessionDto = ClassSessionDto.builder()
					.sessionNo(sessionNo)
					.scheduleNo(request.getScheduleNo())
					.classroomNo(scheduleDto.getClassroomNo())
					.sessionStart(sessionStart)
					.sessionEnd(sessionEnd)
					.sessionStatus(request.getSessionStatus() != null && !request.getSessionStatus().isBlank() 
							? request.getSessionStatus() : ClassSessionDto.STATUS_CLOSED)
				.build();
		
		// insertByAdmin 호출
		classSessionDao.insertByAdmin(sessionDto);

		// [추가] 사후 등록된 세션에도 출석부 생성
		attendanceDao.initAttendance(sessionNo, scheduleDto.getCourseNo());

		// 첫 수업 누락 건 사후 등록 시 개강일 보정
		if (scheduleDto.getScheduleOpen() == null) {
			scheduleDao.updateOpen(request.getScheduleNo(), request.getTargetDate());
		}
	}
	
	// [관리자 2] 특정 세션 상태 수동 변경 ('진행중', '종료', '취소')
	@Override
	@Transactional
	public void updateStatusByAdmin(AdminClassSessionStatusVO request, TokenParseResponseVO parseVO) {
		// [1] 직원 계정인지 1차 차단
		if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
			throw new WhoAreYouException("직원 전용 기능입니다");
		}

		ClassSessionDto session = classSessionDao.selectOneBySessionNo(request.getSessionNo());
		if(session == null) {
			throw new TargetNotfoundException("해당 수업 세션이 존재하지 않습니다");
		}
	
		// [2] 강사(TUTOR) 권한을 가진 사용자인 경우 본인 수업인지 대조
		if (parseVO.getRoleNames() != null && parseVO.getRoleNames().contains("TUTOR")) {
			CourseDto courseDto = courseDao.selectOneByScheduleNo(session.getScheduleNo());
			if (courseDto == null || courseDto.getEmployeeNo() != parseVO.getNoType()) {
				throw new WhoAreYouException("본인이 담당하는 강좌의 세션만 상태를 변경할 수 있습니다");
			}
		}
		
		// [수정] updateStatusOnly 호출 (종료시각 SYSTIMESTAMP 오염 방지)
		classSessionDao.updateStatusOnly(request.getSessionNo(), request.getSessionStatus());
	}
}