package com.kh.khedu.util;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AttendanceDao;
import com.kh.khedu.dao.ClassSessionDao;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dao.ScheduleDao;
import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.dto.ScheduleDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionSchedulerService {
	@Autowired
    private ClassSessionDao classSessionDao;
    @Autowired
    private AttendanceDao attendanceDao;
    @Autowired
    private ScheduleDao scheduleDao;
    @Autowired
    private CourseDao courseDao;
    
    
    /**
     * 10분마다 만료 세션 자동 마감 실행
     */
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void closeExpiredSessions() {
        // [1] 종료 시각이 지났는데 여전히 '진행중'인 세션들 조회
        List<ClassSessionDto> expiredList = classSessionDao.selectExpiredRunningSessions();

        if (expiredList.isEmpty()) {
            return;
        }

        log.info("[스케줄러] 자동 종료 대상 세션 수: {}개", expiredList.size());

        for (ClassSessionDto session : expiredList) {
            int sessionNo = session.getSessionNo();

            // 1. 세션 상태를 '종료'로 변경
            classSessionDao.updateSessionStatusToClosed(sessionNo);

            // 2. 미출결 수강생 일괄 '결석' 처리
            int absentCount = attendanceDao.updateAbsentForUncheckedStudents(sessionNo);

            // 3. 종강일 체크 후 Course 종강 처리 (기존 EndClass 로직과 동일)
            ScheduleDto scheduleDto = scheduleDao.selectOneByScheduleNo(session.getScheduleNo());
            if (scheduleDto != null && scheduleDto.getScheduleClose() != null) {
                LocalDate today = LocalDate.now();
                if (today.isEqual(scheduleDto.getScheduleClose()) || today.isAfter(scheduleDto.getScheduleClose())) {
                    CourseDto courseDto = courseDao.selectOneByCourseNo(scheduleDto.getCourseNo());
                    if (courseDto != null && CourseDto.STATUS_RUNNING.equals(courseDto.getCourseStatus())) {
                        courseDao.updateStatus(scheduleDto.getCourseNo(), CourseDto.STATUS_CLOSED);
                    }
                }
            }

            log.info("[스케줄러] 세션 No.{} 자동 종료 완료 (미출결 -> 결석: {}명)", sessionNo, absentCount);
        }
    }
    
    
    /**
     * 매일 새벽 00:10 당일 수업 세션 및 출석부 일괄 생성
     */
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void autoCreateTodaySessions() {
        String todayKorean = LocalDate.now().getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.NARROW, java.util.Locale.KOREAN);

        // 개강일(schedule_open)이 도달했거나 null이 아닌 오늘 스케줄 조회
        List<ScheduleDto> activeSchedules = scheduleDao.selectTodayActiveSchedules(todayKorean);
        if (activeSchedules == null || activeSchedules.isEmpty()) return;

        LocalDate today = LocalDate.now();

        for (ScheduleDto schedule : activeSchedules) {
            java.time.LocalTime startTime = java.time.LocalTime.parse(schedule.getScheduleStart());
            java.time.LocalTime endTime = java.time.LocalTime.parse(schedule.getScheduleEnd());
            java.sql.Timestamp sessionStart = java.sql.Timestamp.valueOf(today.atTime(startTime));
            java.sql.Timestamp sessionEnd = java.sql.Timestamp.valueOf(today.atTime(endTime));

            // 중복 체크
            ClassSessionDto existing = classSessionDao.selectTodaySession(schedule.getScheduleNo(), sessionStart);
            if (existing != null) continue;

            // 세션 생성
            int sessionNo = classSessionDao.sequence();
            ClassSessionDto sessionDto = ClassSessionDto.builder()
                    .sessionNo(sessionNo)
                    .scheduleNo(schedule.getScheduleNo())
                    .classroomNo(schedule.getClassroomNo())
                    .sessionStart(sessionStart)
                    .sessionEnd(sessionEnd)
                    .build();
            classSessionDao.insert(sessionDto);

            // 출석부 초기화
            attendanceDao.initAttendance(sessionNo, schedule.getCourseNo());
            log.info("[스케줄러] 세션 No.{} 자동 생성 완료 (강좌 No.{})", sessionNo, schedule.getCourseNo());
        }
    }
    
}
