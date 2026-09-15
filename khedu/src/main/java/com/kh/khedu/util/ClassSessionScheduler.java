package com.kh.khedu.util;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
@Component
public class ClassSessionScheduler {
	
	@Autowired
	private ClassSessionDao classSessionDao;
	@Autowired
    private AttendanceDao attendanceDao;
	@Autowired
	private ScheduleDao scheduleDao;
	@Autowired
    private CourseDao courseDao;
	
	
	//매 10분마다 실행 (0분, 10분, 20분, 30분, 40분, 50분)
	//시작 시각(schedule_start)에 도달한 수업을 자동으로 세션 생성 및 '진행중' 처리
	//(사전 취소/수정된 세션이 이미 존재하면 건너뜀)
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional
    public void autoStartSessions() {
        try {
            List<ScheduleDto> targets = classSessionDao.selectAutoStartTargets();
            if (targets == null || targets.isEmpty()) {
                return;
            }

            LocalDate today = LocalDate.now();

            for (ScheduleDto schedule : targets) {
                LocalTime startTime = LocalTime.parse(schedule.getScheduleStart());
                LocalTime endTime = LocalTime.parse(schedule.getScheduleEnd());
                Timestamp sessionStart = Timestamp.valueOf(today.atTime(startTime));
                Timestamp sessionEnd = Timestamp.valueOf(today.atTime(endTime));

                int sessionNo = classSessionDao.sequence();
                ClassSessionDto newSession = ClassSessionDto.builder()
                        .sessionNo(sessionNo)
                        .scheduleNo(schedule.getScheduleNo())
                        .classroomNo(schedule.getClassroomNo())
                        .sessionStart(sessionStart)
                        .sessionEnd(sessionEnd)
                        .sessionStatus("진행중")
                        .build();

                classSessionDao.insert(newSession);

                // 출석부 초기화 (미출결 등록)
                attendanceDao.initAttendance(sessionNo, schedule.getCourseNo());

                // 첫 수업 시 개강일 등록
                if (schedule.getScheduleOpen() == null) {
                    scheduleDao.updateOpenByCourseNo(schedule.getCourseNo(), today);
                }

                // 강좌 상태 갱신
                CourseDto course = courseDao.selectOneByCourseNo(schedule.getCourseNo());
                if (course != null && CourseDto.STATUS_RECRUITING.equals(course.getCourseStatus())) {
                    courseDao.updateStatus(schedule.getCourseNo(), CourseDto.STATUS_RUNNING);
                }

                log.info("[스케줄러] 수업 세션 No.{} 자동 시작 완료 (강좌 No.{})", sessionNo, schedule.getCourseNo());
            }
        } catch (Exception e) {
            log.error("[스케줄러 오류] 세션 자동 시작 중 예외 발생: {}", e.getMessage(), e);
        }
    }
	
    
    /**
     * 매 10분마다 실행
     * 종료 시각(session_end)이 지났으나 종료되지 않은 세션을 자동 마감
     * (미출결자 자동 결석 + 종강일 도달 시 강좌 자동 종강)
     */
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional
    public void autoCloseSessions() {
        try {
            List<ClassSessionDto> expiredList = classSessionDao.selectExpiredRunningSessions();
            if (expiredList == null || expiredList.isEmpty()) {
                return;
            }

            log.info("[스케줄러] 자동 종료 대상 세션 수: {}개", expiredList.size());

            for (ClassSessionDto session : expiredList) {
                int sessionNo = session.getSessionNo();

                // 1. 세션 상태를 '종료'로 변경
                classSessionDao.updateSessionStatusToClosed(sessionNo);

                // 2. 미출결 수강생 일괄 '결석' 처리
                int absentCount = attendanceDao.updateAbsentForUncheckedStudents(sessionNo);

                // 3. 종강일 체크 후 Course 종강 처리
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
        } catch (Exception e) {
            log.error("[스케줄러 오류] 세션 자동 마감 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
