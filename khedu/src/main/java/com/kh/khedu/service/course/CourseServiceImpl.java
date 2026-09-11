package com.kh.khedu.service.course;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dao.ClassSessionDao;
import com.kh.khedu.dao.ClassroomDao;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dao.GradeDao;
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dao.ScheduleDao;
import com.kh.khedu.dao.TutorDao;
import com.kh.khedu.dto.ClassSessionDto;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.enums.AccountType;
import com.kh.khedu.error.AlreadyExistsException;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.error.WhoAreYouException;
import com.kh.khedu.service.AssignmentService;
import com.kh.khedu.service.ExamService;
import com.kh.khedu.service.attendance.AttendanceService;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.attendance.SessionAttendanceDetailVO;
import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailResponseVO;
import com.kh.khedu.vo.course.CourseFormDataVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.course.CourseSearchVO;
import com.kh.khedu.vo.course.CourseSimpleListVO;
import com.kh.khedu.vo.course.StudentCourseListVO;
import com.kh.khedu.vo.exam.ExamListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;
import com.kh.khedu.vo.schedule.ScheduleCreateRequestVO;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseDao courseDao;
	@Autowired
	private ScheduleDao scheduleDao;
	@Autowired
	private ClassroomDao classroomDao;
	@Autowired
	private TutorDao tutorDao;
	@Autowired
	private AcademySubjectDao academySubjectDao;
	@Autowired
	private GradeDao gradeDao;
	@Autowired
	private ClassSessionDao classSessionDao;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ExamService examService;
	@Autowired
	private ParentStudentDao parentStudentDao;
	
	
	//	공통메소드
	//학부모-자녀 관계 확인
    private void checkParentStudent(
    		int parentNo,
    		int studentNo) {
    	List<ParentStudentVO> studentList = 
    			parentStudentDao.findByParentNo(parentNo);
    	
    	boolean connected = 
    			studentList.stream()
    				.anyMatch(student ->
    						student.getStudentNo() == studentNo
    				);
    	if(!connected) {
    		throw new GetOutException();
    	}
    }
	
	//강좌 등록화면 진입 시 최초 조회
	@Override
	public CourseFormDataVO getCourseFormData() {
		return CourseFormDataVO.builder()
					.subjectList(academySubjectDao.subjectList())
					.gradeList(gradeDao.selectList())
					.classroomList(classroomDao.classroomListWhenRegister())
					.tutorList(tutorDao.tutorListBySubject())
				.build();
	}
	
	//필터링 된 강의실 목록 조회
	@Override
	public List<ClassroomWhenRegisterVO> getAvailAbleClassrooms(AvailableClassroomRequestVO request) {
		return classroomDao.selectAvailableClassroomList(request);
	}
	
	//강좌 등록
	@Override
	@Transactional
	public void createCourse(
			TokenParseResponseVO parseVO,
			CourseCreateRequestVO request) {
		
		//[0] 등록하는 자가 누구인지 확인
		

		
		//[1] 강사 - 과목 관계 검증
		int tutorSubjectCount = tutorDao.checkTutorSubject(request.getEmployeeNo(), request.getAcademySubjectNo());
		
		if(tutorSubjectCount == 0) {
			throw new TargetNotfoundException("선택한 강사가 해당 과목을 담당하지 않습니다");
		}
		
		//[2] 요청 내부 일정 검증
		validateRequestSchedules(request);
		
		//[3] 기존 DB 일정 검증
		for (ScheduleCreateRequestVO schedule : request.getSchedules()) {
			
			// 강사 기존 수업 충돌
			int tutorConflict = scheduleDao.checkTutorScheduleConflict(request.getEmployeeNo(), schedule);
			
			if(tutorConflict > 0) {
				throw new AlreadyExistsException("담당 강사의 기존 수업과 시간이 겹칩니다");
			}
			
			// 강의실 사용 가능 여부(수용인원수)
			int classroomCount = classroomDao.checkClassroom(schedule.getClassroomNo(), request.getCourseLimit());
			if(classroomCount == 0) {
				throw new AlreadyExistsException("선택한 강의실을 사용할 수 없습니다");
			}
			
			// 강의실 기존 수업 충돌
			int classroomConflict = scheduleDao.checkClassroomScheduleConflict(schedule.getClassroomNo(), schedule);
			
			if(classroomConflict > 0) {
				throw new AlreadyExistsException("선택한 강의실의 기존 수업과 시간이 겹칩니다");
			}
		}
		
		//[4] CourseVO 생성		
		int courseNo = courseDao.sequence();
		
		//academySubjectNo가 안들어가기 때문에 명시적으로 작성해줌
		CourseDto courseDto = CourseDto.builder()
						.courseNo(courseNo)
						.employeeNo(request.getEmployeeNo())
						.gradeNo(request.getGradeNo())
						.courseTitle(request.getCourseTitle())
						.courseSubject(request.getCourseSubject())
						.courseLimit(request.getCourseLimit())
						.courseFee(request.getCourseFee())
						.courseInfo(request.getCourseInfo())
						.courseType(request.getCourseType())
					.build();
		
		//course등록
		courseDao.insertCourse(courseDto);
		
		
		//[5] ScheduleVO 생성 및 insert
		for(ScheduleCreateRequestVO requestSchedule : request.getSchedules()) {
			int scheduleNo = scheduleDao.sequence();
			
			ScheduleDto schedule = new ScheduleDto();
			
			BeanUtils.copyProperties(requestSchedule, schedule);
			
			schedule.setScheduleNo(scheduleNo);
			schedule.setCourseNo(courseNo);
			
			scheduleDao.insertSchedule(schedule);
		}
	}
	
	//등록 시 요청 내부 schedule  검증
	private void validateRequestSchedules(CourseCreateRequestVO request) {
		
		// validateRequestSchedules 메서드 맨 위
		if (request.getSchedules() == null || request.getSchedules().isEmpty()) {
		    throw new IllegalArgumentException("최소 하나 이상의 수업 일정을 등록해야 합니다.");
		}
		
		for(int i = 0; i < request.getSchedules().size(); i++) {
			ScheduleCreateRequestVO a = request.getSchedules().get(i);
			
			// 등록하려는 일정의 시작일 <= 종료일 // 둘 중 하나가 null이면 검사x
			
			if(hasValue(a.getScheduleOpen()) && hasValue(a.getScheduleClose())) {
				if (a.getScheduleOpen()
			            .isAfter(a.getScheduleClose())) {
			        throw new IllegalArgumentException(
			                "수업 시작일은 종료일보다 늦을 수 없습니다."
			        );
			    }
			}
			
			// 시작 시간 < 종료시간 
			
			if(a.getScheduleStart().compareTo(a.getScheduleEnd()) >= 0) {
				throw new IllegalArgumentException("수업 시작시간은 종료시간보다 빨라야 합니다");
			}
			
			// 다른 Schedule과 비교
			for(int j = i + 1; j < request.getSchedules().size(); j++) {
				ScheduleCreateRequestVO b = request.getSchedules().get(j);
				
				//날짜가 겹침
				boolean dateOverlap = isDateOverlap(a, b);
				
				//요일이 같은 경우
				boolean weekOverlap = a.getScheduleWeek().equals(b.getScheduleWeek());
				
				boolean timeOverlap = 
						//      |--------------------|
						//   			|===============|
						// 			|==============|
						//  |=========|
						a.getScheduleStart().compareTo(b.getScheduleEnd()) < 0
						&&
						a.getScheduleEnd().compareTo(b.getScheduleStart()) > 0;
						
				//담당강사는 강의당 한 명이므로 모든 schedule이 같은 강사를 사용
						
				if(dateOverlap && weekOverlap && timeOverlap ) {
					throw new IllegalArgumentException("등록하려는 수업 일정끼리 시간이 겹칩니다");
				}
				
			}
			
		}
	}
	
	//날짜 겹침 검사
	private boolean isDateOverlap(ScheduleCreateRequestVO a, ScheduleCreateRequestVO b) {
		//null인 경우는 기간 제한이 없는 것으로 처리
		boolean firstCondition = //개강일이 종강일과 작거나 같다
				a.getScheduleOpen() == null
				|| b.getScheduleClose() == null
				|| !a.getScheduleOpen().isAfter(b.getScheduleClose()); // a <= b
				
		boolean secondCondition = //종강일이 개강일과 크거나 같다
				a.getScheduleClose() == null
				|| b.getScheduleOpen() == null
				|| !a.getScheduleClose().isBefore(b.getScheduleOpen()); // a >= b
		
		return firstCondition && secondCondition;
	}
	private boolean hasValue(LocalDate value) {
	    return value != null;
	}

	//강좌 목록
	@Override
	public List<CourseListVO> getCourseList() {
		return courseDao.selectCourseList();
	}

	//강좌 검색조회
	@Override
	public PageResponseVO<CourseListVO> selectList(CourseSearchVO search) {
		
		// 현재 페이지의 강좌 목록 조회
		List<CourseListVO> list = courseDao.selectSearchList(search);
		
		// 검색 조건에 해당하는 전체 강좌 수
		int totalCount = courseDao.selectCount(search);
		
		//페이지 정보까지 계산하여 반환
		return new PageResponseVO<>(
				list,
				totalCount,
				search
		);
	}
	
	//강좌 상세
	@Override
	public CourseDetailResponseVO getCourseDetail(int courseNo, TokenParseResponseVO parseVO) {
		// [0] 직원 계정 1차 검증
        if (!AccountType.EMPLOYEE.getDescription().equals(parseVO.getAccountType())) {
            throw new WhoAreYouException("직원 전용 기능입니다");
        }

        // [1] 강좌 정보 조회
        CourseDto course = courseDao.selectOneByCourseNo(courseNo);
        if (course == null) {
            throw new TargetNotfoundException("해당 강좌가 존재하지 않습니다");
        }

        // [1-1] 강사(TUTOR) 권한일 경우 본인 강좌인지 검증 (ADMIN/DESK는 프리패스)
        List<String> roles = parseVO.getRoleNames();
        boolean isManager = roles != null && (roles.contains("ADMIN") || roles.contains("DESK"));
        if (!isManager && roles != null && roles.contains("TUTOR")) {
            if (course.getEmployeeNo() != parseVO.getNoType()) {
                throw new WhoAreYouException("본인이 담당하는 강좌만 열람할 수 있습니다");
            }
        } else if (!isManager) {
            throw new WhoAreYouException("강좌를 조회할 권한이 없습니다");
        }
		
        // [1-2] 강사 이름 및 스케줄 목록 조회
        String tutorName = courseDao.selectTutorNameByEmployeeNo(course.getEmployeeNo());
        List<ScheduleDto> scheduleList = scheduleDao.selectListByCourseNo(courseNo);
        
     // [2] 오늘 날짜에 해당하는 세션 및 출결 현황 탐색
        ClassSessionDto todaySession = null;
        SessionAttendanceDetailVO attendanceDetail = null;

        LocalDate today = LocalDate.now();
        String todayWeek = today.getDayOfWeek()
                .getDisplayName(TextStyle.NARROW, Locale.KOREAN); // '월', '화', ..., '금'

        for (ScheduleDto schedule : scheduleList) {
            // 1. 오늘 요일과 일치하지 않는 스케줄은 DB 조회 생략
            if (!todayWeek.equals(schedule.getScheduleWeek())) {
                continue;
            }

            LocalTime startTime = LocalTime.parse(schedule.getScheduleStart());
            Timestamp sessionStart = Timestamp.valueOf(today.atTime(startTime));

            // 오늘 날짜 + 시작 시간으로 등록된 세션 확인
            ClassSessionDto session = classSessionDao.selectTodaySession(schedule.getScheduleNo(), sessionStart);
            if (session != null) {
                todaySession = session;
                attendanceDetail = attendanceService.getSessionAttendanceDetail(session.getSessionNo(), parseVO);
                
                // 현재 '진행중'인 세션을 찾았다면 즉시 루프 종료
                if (ClassSessionDto.STATUS_RUNNING.equals(session.getSessionStatus())) {
                    break;
                }
            }
        }
        
        // [3] 과제 정보 조회
        List<AssignmentListVO> assignmentList = assignmentService.selectRecentListByCourse(courseNo);
        // [4] 시험 정보 조회
        List<ExamListVO> examList = examService.selectRecentListByCourse(courseNo);
        
        // null 방어 처리
        if (assignmentList == null) assignmentList = Collections.emptyList();
        if (examList == null) examList = Collections.emptyList();
        
        // [3] 통합 응답 객체 생성 (3, 4번 과제/시험은 빈 리스트 유지)
        return CourseDetailResponseVO.builder()
                .courseInfo(course)
                .tutorName(tutorName)
                .scheduleList(scheduleList)
                .todaySession(todaySession)
                .attendanceDetail(attendanceDetail)
                .assignmentList(assignmentList) // 과제 팀원 영역 (비워둠)
                .examList(examList)       // 시험 팀원 영역 (비워둠)
                .build();
	}

	@Override
	public List<StudentCourseListVO> selectListByStudent(int studentNo) {
	    return courseDao.selectListByStudent(studentNo);
	}
	
	@Override
	public List<StudentCourseListVO> selectListByParentStudent(
	        int parentNo,
	        int studentNo) {

	    //부모-자녀 관계 확인
	    checkParentStudent(parentNo, studentNo);

	    return courseDao.selectListByStudent(studentNo);
	}
	
	@Override
	public List<CourseSimpleListVO> selectManageCourseList(
	        int employeeNo,
	        boolean tutor) {
	    if(tutor) {
	        return courseDao.selectManageCourseListByEmployee(employeeNo);
	    }
	    return courseDao.selectManageCourseList();
	}

}
