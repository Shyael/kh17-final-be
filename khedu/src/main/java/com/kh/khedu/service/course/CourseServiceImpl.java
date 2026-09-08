package com.kh.khedu.service.course;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dao.ClassroomDao;
import com.kh.khedu.dao.CourseDao;
import com.kh.khedu.dao.GradeDao;
import com.kh.khedu.dao.ScheduleDao;
import com.kh.khedu.dao.TutorDao;
import com.kh.khedu.dto.CourseDto;
import com.kh.khedu.dto.ScheduleDto;
import com.kh.khedu.error.AlreadyExistsException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.classroom.AvailableClassroomRequestVO;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;
import com.kh.khedu.vo.course.CourseCreateRequestVO;
import com.kh.khedu.vo.course.CourseDetailVO;
import com.kh.khedu.vo.course.CourseFormDataVO;
import com.kh.khedu.vo.course.CourseListVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
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
	
	
	//강좌 상세
	@Override
	public CourseDetailVO getCourseDetail(int courseNo) {
		return courseDao.selectCourseDetail(courseNo);
	}

}
