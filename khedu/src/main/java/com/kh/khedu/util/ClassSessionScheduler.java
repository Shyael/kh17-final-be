package com.kh.khedu.util;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.ClassSessionDao;
import com.kh.khedu.dao.ScheduleDao;
import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.ScheduleDto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ClassSessionScheduler {
	
	@Autowired
	private ClassSessionDao classSessionDao;
	@Autowired
	private ScheduleDao scheduleDao;
	@Autowired
    private SessionSchedulerService sessionSchedulerService;
	
	//매일 새벽 00:05실행
	@Scheduled(cron = "0 5 0 * * *")
	@Transactional
	public void processClassSessions() {
		//[방어 2] 강사가 깜빡하고 안 누른 지난 세션 일괄 자동 '종료'
		classSessionDao.autoCloseExpiredSessions();
		
		//[누락 보충] 서버 장애 등으로 생성되지 못한 세션 보충 생성
		// 진행중인 강좌, 강사가 최소 1회 이상 수업시작을 눌러 실제 개강이 확정된 스케줄만 대상, 
		// 종강일 미정인 경우, 종강일이 정해진 경우 오늘 날짜를 지나지 않은 스케줄만 가져오는 구문
		List<ScheduleDto> activeSchedules = scheduleDao.selectActiveSchedules();
		LocalDate today = LocalDate.now();
		
		for(ScheduleDto schedule : activeSchedules) {
			LocalDate startDate = schedule.getScheduleOpen();
			LocalDate closeDate = schedule.getScheduleClose();
			
			//개강 전 (첫 수업 전)이면 세션 생성 대상 아님
			if(startDate == null) continue;
			
			LocalDate checkDate = startDate;
			while(!checkDate.isAfter(today)) { // 즉, checkDate <= today인 동안 반복한다
				//종강일이 설정되어 있고 검사일이 종강일 이후라면 중단
				if (closeDate != null && checkDate.isAfter(closeDate)) {
					break;
				}
				
				//해당요일 일치여부 확인
				String dayOfWeek = checkDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);
				if(dayOfWeek.equals(schedule.getScheduleWeek())) {
					LocalTime startTime = LocalTime.parse(schedule.getScheduleStart());
					LocalTime endTime = LocalTime.parse(schedule.getScheduleEnd());
					Timestamp sessionStart = Timestamp.valueOf(checkDate.atTime(startTime));
                    Timestamp sessionEnd = Timestamp.valueOf(checkDate.atTime(endTime));
                    
                    //이미 존재하는 지 확인
                    ClassSessionDto existSession = 
                    		classSessionDao.selectTodaySession(schedule.getScheduleNo(), sessionStart);
                    
                    //누락된 회차라면 insert
                    if(existSession == null) {
                    	int sessionNo = classSessionDao.sequence();
                    	
                    	//지나간 날짜는 '종료', 오늘 날짜면 '진행중'
                    	String status = checkDate.isBefore(today) ? "종료" : "진행중";
                    	
                    	ClassSessionDto newSession = ClassSessionDto.builder()
                    				.sessionNo(sessionNo)
                    				.scheduleNo(schedule.getScheduleNo())
                    				.classroomNo(schedule.getClassroomNo())
                    				.sessionStart(sessionStart)
                    				.sessionEnd(sessionEnd)
                    				.sessionStatus(status)
                    			.build();
                    	
                    	classSessionDao.insert(newSession);
                    }
				}
				checkDate = checkDate.plusDays(1);
			}
		}
	}
	
	/**
     * 매 10분마다 실행 (0분, 10분, 20분, 30분, 40분, 50분)
     * 종료 시각(session_end)이 지났으나 종료되지 않은 세션을 자동 마감
     */
    @Scheduled(cron = "0 0/10 * * * *")
    public void autoCloseSessions() {
        try {
            sessionSchedulerService.closeExpiredSessions();
        } catch (Exception e) {
            log.error("[스케줄러 오류] 세션 자동 마감 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
