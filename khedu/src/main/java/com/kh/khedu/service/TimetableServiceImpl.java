package com.kh.khedu.service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.ClassSessionDao;
import com.kh.khedu.dao.ScheduleDao;
import com.kh.khedu.vo.classSession.WeeklyClassSessionVO;
import com.kh.khedu.vo.schedule.WeeklyScheduleVO;
import com.kh.khedu.vo.schedule.WeeklyTimetableVO;

@Service
public class TimetableServiceImpl implements TimetableService {

	@Autowired
	private ScheduleDao scheduleDao;
	
	@Autowired
	private ClassSessionDao classSessionDao;
	
	@Override
	public List<WeeklyTimetableVO> getWeeklyTimetable(int classroomNo, LocalDate date) {
		
		//1. 조회 기준 날짜가 포함된 주의 월요일
		LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		
		//일요일
		LocalDate weekEnd = weekStart.plusDays(6);
		
		//class_session 조회용
		Timestamp sessionStart = Timestamp.valueOf(weekStart.atStartOfDay());
		
		//다음 주 월요일 00:00 (미포함)
		Timestamp sessionEnd = Timestamp.valueOf(weekStart.plusDays(7).atStartOfDay());
		
		// 2. 이번 주 기본 반복 스케줄 조회
        List<WeeklyScheduleVO> schedules = scheduleDao.selectWeeklySchedules(weekStart, weekEnd);

        System.out.println("스케줄"+ schedules);
        
        // 3. 이번 주 실제 생성된 수업 조회
        List<WeeklyClassSessionVO> sessions = classSessionDao.selectWeeklySessions( sessionStart, sessionEnd);

        // 4. scheduleNo + 날짜로 실제 수업 검색할 수 있도록 Map 구성
        Map<String, WeeklyClassSessionVO> sessionMap = new HashMap<>();

        for (WeeklyClassSessionVO session : sessions) {
            LocalDate sessionDate = session.getSessionStart().toLocalDateTime().toLocalDate();

            String key = makeKey(session.getScheduleNo(), sessionDate);

            sessionMap.put(key, session);
        }

        // 5. 최종 시간표 생성
        List<WeeklyTimetableVO> result = new ArrayList<>();

        for (WeeklyScheduleVO schedule : schedules) {

            // 월/화/수... → 실제 날짜 계산
            LocalDate classDate = getClassDate (weekStart, schedule.getScheduleWeek());

            // 해당 날짜가 개강일 이전이면 제외
            if (schedule.getScheduleOpen() != null && classDate.isBefore(schedule.getScheduleOpen())) {
                continue;
            }

            // 해당 날짜가 종강일 이후면 제외
            if (schedule.getScheduleClose() != null && classDate.isAfter(schedule.getScheduleClose())) {
                continue;
            }

            String key = makeKey(schedule.getScheduleNo(), classDate);

            WeeklyClassSessionVO session = sessionMap.get(key);

            WeeklyTimetableVO timetable;

            // 6. 실제 class_session이 존재하는 경우
            if (session != null) {
                LocalDateTime startDateTime = session.getSessionStart().toLocalDateTime();

                LocalDateTime endDateTime = session.getSessionEnd().toLocalDateTime();

                timetable = WeeklyTimetableVO
                		.builder()
	                        .scheduleNo(schedule.getScheduleNo())
	                        .sessionNo(session.getSessionNo())
	                        .courseNo(schedule.getCourseNo())
	                        .courseTitle(schedule.getCourseTitle())
	                        .employeeNo(schedule.getEmployeeNo())
	                        .employeeName(schedule.getEmployeeName())
	                        // 실제 세션의 강의실 사용
	                        .classroomNo(session.getClassroomNo())
	                        .classroomName(session.getClassroomName())
	                        .classDate(startDateTime.toLocalDate())
	                        .startTime(startDateTime.toLocalTime())
	                        .endTime(endDateTime.toLocalTime())
	                        .classStatus(resolveSessionStatus(session))
                        .build();
            }

            // 7. class_session이 아직 없는 경우
            else {

                LocalTime startTime = LocalTime.parse(schedule.getScheduleStart());

                LocalTime endTime = LocalTime.parse(schedule.getScheduleEnd());

                timetable = WeeklyTimetableVO
                		.builder()
	                        .scheduleNo(schedule.getScheduleNo())
	                        .sessionNo(null)
	                        .courseNo(schedule.getCourseNo())
	                        .courseTitle(schedule.getCourseTitle())
	                        .employeeNo(schedule.getEmployeeNo())
	                        .employeeName(schedule.getEmployeeName())
	                        // 기본 schedule 강의실 사용
	                        .classroomNo(schedule.getClassroomNo())
	                        .classroomName(schedule.getClassroomName())
	                        .classDate(classDate)
	                        .startTime(startTime)
	                        .endTime(endTime)
	                        .classStatus("예정")
                        .build();
            }

            	// 8. 최종 실제 강의실 기준으로 필터
	            if (timetable.getClassroomNo() != null && timetable.getClassroomNo() == classroomNo) {
	                result.add(timetable);
	            }
	        }
	
	        // 9. 날짜 → 시간 순 정렬
	        result.sort((a, b) -> {int dateCompare = a.getClassDate().compareTo(b.getClassDate());
	                    if (dateCompare != 0) {
	                        return dateCompare;
	                    }
	
	                    return a.getStartTime().compareTo(b.getStartTime());
	                }
		        );
		
		        return result;
		    }
		
		    // scheduleNo + 날짜 조합
		    private String makeKey(int scheduleNo, LocalDate date) {
		        return scheduleNo + "_" + date;
		    }
		
		    // 요일 → 이번 주 실제 날짜
		    private LocalDate getClassDate(LocalDate weekStart, String scheduleWeek) {
		        return switch (scheduleWeek) {
		
		            case "월" -> weekStart;
		            case "화" -> weekStart.plusDays(1);
		            case "수" -> weekStart.plusDays(2);
		            case "목" -> weekStart.plusDays(3);
		            case "금" -> weekStart.plusDays(4);
		            case "토" -> weekStart.plusDays(5);
		            case "일" -> weekStart.plusDays(6);
		
		            default -> throw new IllegalArgumentException("올바르지 않은 수업 요일입니다: " + scheduleWeek);
		        };
		    }
		
		    // 시간표 화면용 상태 계산
		    private String resolveSessionStatus(WeeklyClassSessionVO session) {
		        // 취소는 시간과 관계없이 취소
		        if ("취소".equals(session.getSessionStatus())) {
		            return "취소";
		        }
		        // DB에서 이미 종료 처리된 경우
		        if ("종료".equals(session.getSessionStatus())) {
		            return "종료";
		        }
		
		        LocalDateTime now = LocalDateTime.now();
		
		        LocalDateTime start = session.getSessionStart().toLocalDateTime();
		
		        LocalDateTime end = session.getSessionEnd().toLocalDateTime();
		
		        // 새벽 00:05에 세션이 생성되어
		        // DB 상태가 진행중이어도 아직 시작 전이면 예정
		        if (now.isBefore(start)) {
		            return "예정";
		        }
		
		        if (now.isBefore(end)) {
		            return "진행중";
		        }
		
		        return "종료";
	}

}
