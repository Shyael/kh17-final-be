package com.kh.khedu.util;

import java.time.LocalDate;
import java.util.List;

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
     * 만료된 세션 자동 종료 및 결석 일괄 처리
     */
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
}
