package com.kh.khedu.service.workschedule;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.dao.payroll.EmployeeWorkScheduleDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;
import com.kh.khedu.dto.payroll.EmployeeWorkScheduleDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleAddResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleMonthlySummaryVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleSearchResponseVO;

@Service
@Transactional
public class EmployeeWorkScheduleServiceImpl
        implements EmployeeWorkScheduleService {

    @Autowired
    private EmployeeWorkScheduleDao employeeWorkScheduleDao;

    @Autowired
    private EmployeeAttendanceDao employeeAttendanceDao;
    
    @Autowired
    private ContractDao contractDao;


    // 근무 일정 등록
    @Override
    public WorkScheduleAddResponseVO add(
            WorkScheduleAddRequestVO requestVO) {
    	
    	ContractDto existContract = contractDao.find(requestVO.getContractNo());
    	if(existContract==null) throw new TargetNotfoundException();
    	
    	if(requestVO.getScheduledWorkDate()==null||requestVO.getScheduledDayType()==null)
    		throw new GetOutException();
    	
    	LocalDateTime converter = requestVO.getScheduledWorkDate().toLocalDateTime();
    	LocalDate date = converter.toLocalDate();
    	LocalDate contractStart = existContract.getContractStart().toLocalDateTime().toLocalDate();
    	LocalDate contractEnd = existContract.getContractEnd().toLocalDateTime().toLocalDate();
    	boolean contractPeriod =
    	        !date.isBefore(contractStart)
    	        && !date.isAfter(contractEnd);

    	if (!contractPeriod) {
    	    throw new GetOutException();
    	}
    	
    	if (requestVO.getScheduledDayType() != null) {

    	    if (!"workday".equals(
    	            requestVO.getScheduledDayType())
    	            && !"holiday".equals(
    	                    requestVO.getScheduledDayType())
    	            && !"dayOff".equals(
    	                    requestVO.getScheduledDayType())) {

    	        throw new GetOutException();
    	    }
    	}
    	boolean hasSchedule = requestVO.getScheduledClockIn()!=null&&requestVO.getScheduledClockOut()!=null;
    	boolean canWork = requestVO.getScheduledDayType().equals("workday")||requestVO.getScheduledDayType().equals("holiday");
    	if(canWork==true&&hasSchedule==false) throw new GetOutException();
    	
    	boolean dayOff = requestVO.getScheduledDayType().equals("dayOff");
    	if(dayOff) {
    	    requestVO.setScheduledClockIn(null);
    	    requestVO.setScheduledClockOut(null);
    	}
    	
    	int key =existContract.getEmployeeNo();
    	EmployeeWorkScheduleDto schedule= employeeWorkScheduleDao.isExist(key, requestVO.getScheduledWorkDate());
    	if(schedule!=null) throw new GetOutException();
    	
    long	workScheduleNo =	employeeWorkScheduleDao.sequence();
    	

requestVO.setWorkScheduleNo(
        workScheduleNo);
    	
    	
    	boolean success = employeeWorkScheduleDao.add(requestVO);
    	if(!success) throw new GetOutException();
    	WorkScheduleAddResponseVO newSchedule=WorkScheduleAddResponseVO.builder()
    		.scheduledClockIn(requestVO.getScheduledClockIn())
    		.scheduledClockOut(requestVO.getScheduledClockOut())
    		.scheduledDayType(requestVO.getScheduledDayType())
    		.scheduledWorkDate(requestVO.getScheduledWorkDate())
    		.build();
    	
    	return newSchedule;
    	
    	
    }

  // 근무 일정 수정
	@Override
	@Transactional
	public void update(
        WorkScheduleUpdateRequestVO requestVO) {

    // 수정할 근무 일정 조회
    EmployeeWorkScheduleDto currentSchedule =
            employeeWorkScheduleDao.findByNo(
                    requestVO.getWorkScheduleNo());

    if (currentSchedule == null) {
        throw new TargetNotfoundException();
    }


    // 예정 근무일 유형 검증
    if (requestVO.getScheduledDayType() != null) {

        if (!"workday".equals(
                requestVO.getScheduledDayType())
                && !"holiday".equals(
                        requestVO.getScheduledDayType())
                && !"dayOff".equals(
                        requestVO.getScheduledDayType())) {

            throw new GetOutException();
        }
    }


    // 수정할 근무 날짜가 있다면 날짜 단위로 정규화
    boolean workDateChanged = false;

    if (requestVO.getScheduledWorkDate() != null) {

        LocalDate requestWorkDate =
                requestVO.getScheduledWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();

        LocalDate currentWorkDate =
                currentSchedule.getScheduledWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        requestVO.setScheduledWorkDate(
                Timestamp.valueOf(
                        requestWorkDate.atStartOfDay()));


        workDateChanged =
                !requestWorkDate.equals(
                        currentWorkDate);
    }


    // 예정 근무일 유형이 실제로 변경되는지 확인
    boolean dayTypeChanged =
            requestVO.getScheduledDayType() != null
            && !requestVO.getScheduledDayType()
                    .equals(
                            currentSchedule.getScheduledDayType());


    // 이미 실제 근태가 발생한 스케줄인지 확인
    EmployeeAttendanceDto attendanceExist =
            employeeAttendanceDao.findBySchedule(
                    currentSchedule.getWorkScheduleNo());


    // 실제 근태가 존재하면
    // 근무 날짜 / 근무일 유형 변경 불가
    if (attendanceExist != null
            && (workDateChanged
            || dayTypeChanged)) {

        throw new GetOutException();
    }


    // 근무 날짜가 실제로 변경되는 경우
    if (workDateChanged) {

        ContractDto contract =
                contractDao.find(
                        currentSchedule.getContractNo());

        if (contract == null) {
            throw new TargetNotfoundException();
        }


        LocalDate contractStart =
                contract.getContractStart()
                        .toLocalDateTime()
                        .toLocalDate();

        LocalDate contractEnd =
                contract.getContractEnd() == null
                        ? null
                        : contract.getContractEnd()
                                .toLocalDateTime()
                                .toLocalDate();

        LocalDate workDate =
                requestVO.getScheduledWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // 변경할 날짜가 계약기간 안인지 확인
        boolean contractPeriod =
                !workDate.isBefore(
                        contractStart)
                && (contractEnd == null
                || !workDate.isAfter(
                        contractEnd));

        if (!contractPeriod) {
            throw new GetOutException();
        }


        // 같은 직원에게 변경할 날짜의 일정이 이미 있는지 확인
        EmployeeWorkScheduleDto alreadyExist =
                employeeWorkScheduleDao.isExist(
                        contract.getEmployeeNo(),
                        requestVO.getScheduledWorkDate());

        if (alreadyExist != null) {
            throw new GetOutException();
        }
    }


    // 수정 후 적용될 근무일 유형
    String scheduledDayType =
            requestVO.getScheduledDayType() != null
                    ? requestVO.getScheduledDayType()
                    : currentSchedule.getScheduledDayType();


    // 휴무일로 변경
    if ("dayOff".equals(
            scheduledDayType)) {

        // dayOff로 변경 요청한 경우
        // Mapper에서 예정시간을 null 처리할 수 있도록 유지
        if ("dayOff".equals(
                requestVO.getScheduledDayType())) {

            requestVO.setScheduledClockIn(
                    null);

            requestVO.setScheduledClockOut(
                    null);
        }
    }
    else {

        // 수정 후 최종적으로 적용될 예정 출근시간
        Timestamp scheduledClockIn =
                requestVO.getScheduledClockIn() != null
                        ? requestVO.getScheduledClockIn()
                        : currentSchedule.getScheduledClockIn();


        // 수정 후 최종적으로 적용될 예정 퇴근시간
        Timestamp scheduledClockOut =
                requestVO.getScheduledClockOut() != null
                        ? requestVO.getScheduledClockOut()
                        : currentSchedule.getScheduledClockOut();


        // workday / holiday는 예정 출퇴근시간 필요
        if (scheduledClockIn == null
                || scheduledClockOut == null) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                scheduledClockIn.toLocalDateTime();

        LocalDateTime clockOut =
                scheduledClockOut.toLocalDateTime();


        // 예정 퇴근시간은 예정 출근시간 이후
        if (!clockOut.isAfter(
                clockIn)) {

            throw new GetOutException();
        }
    }


    // actual 값 음수 방지
    if (requestVO.getActualWorkHours() != null
            && requestVO.getActualWorkHours() < 0) {

        throw new GetOutException();
    }


    if (requestVO.getActualOvertimeHours() != null
            && requestVO.getActualOvertimeHours() < 0) {

        throw new GetOutException();
    }


    if (requestVO.getActualNightHours() != null
            && requestVO.getActualNightHours() < 0) {

        throw new GetOutException();
    }


    if (requestVO.getActualHolidayHours() != null
            && requestVO.getActualHolidayHours() < 0) {

        throw new GetOutException();
    }


    // 근무 일정 부분 수정
    boolean success =
            employeeWorkScheduleDao.update(
                    requestVO);

    if (!success) {
        throw new TargetNotfoundException();
    }
	}


    // 직원 특정 날짜 근무 일정 조회
    @Override
    @Transactional(readOnly = true)
    public WorkScheduleResponseVO find(
            EmployeeSearchByNameVO employeeVO,
            Timestamp scheduledWorkDate) {

        if (employeeVO == null
                || scheduledWorkDate == null) {

            throw new GetOutException();
        }


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        scheduledWorkDate);

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        return WorkScheduleResponseVO.builder()
                .scheduledWorkDate(
                        scheduleDto.getScheduledWorkDate())
                .scheduledClockIn(
                        scheduleDto.getScheduledClockIn())
                .scheduledClockOut(
                        scheduleDto.getScheduledClockOut())
                .scheduledDayType(
                        scheduleDto.getScheduledDayType())
                .actualWorkHours(
                        scheduleDto.getActualWorkHours())
                .actualOvertimeHours(
                        scheduleDto.getActualOvertimeHours())
                .actualNightHours(
                        scheduleDto.getActualNightHours())
                .actualHolidayHours(
                        scheduleDto.getActualHolidayHours())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public WorkScheduleSearchResponseVO search(
            long employeeNo,
            Timestamp startDate,
            Timestamp endDate) {

        if (startDate == null
                || endDate == null) {

            throw new GetOutException();
        }


        if (!endDate.after(
                startDate)) {

            throw new GetOutException();
        }


        List<WorkScheduleResponseVO> scheduleList =
                employeeWorkScheduleDao.search(
                        employeeNo,
                        startDate,
                        endDate);


        double totalWorkHours = 0;
        double totalOvertimeHours = 0;
        double totalNightHours = 0;
        double totalHolidayHours = 0;


        for (WorkScheduleResponseVO scheduleVO
                : scheduleList) {

            totalWorkHours +=
                    scheduleVO.getActualWorkHours();

            totalOvertimeHours +=
                    scheduleVO.getActualOvertimeHours();

            totalNightHours +=
                    scheduleVO.getActualNightHours();

            totalHolidayHours +=
                    scheduleVO.getActualHolidayHours();
        }


        WorkScheduleMonthlySummaryVO summaryVO =
                WorkScheduleMonthlySummaryVO.builder()
                        .totalWorkHours(
                                totalWorkHours)
                        .totalOvertimeHours(
                                totalOvertimeHours)
                        .totalNightHours(
                                totalNightHours)
                        .totalHolidayHours(
                                totalHolidayHours)
                        .build();


        return WorkScheduleSearchResponseVO.builder()
                .scheduleList(
                        scheduleList)
                .summary(
                        summaryVO)
                .build();
    }
    
    @Override
    @Transactional
    public void autoAbsent() {

        Timestamp now =
                Timestamp.valueOf(
                        LocalDateTime.now());


        // 예정 퇴근시간이 지났는데
        // 근태가 없는 근무일 조회
        List<EmployeeWorkScheduleDto> scheduleList =
                employeeWorkScheduleDao
                        .findAutoAbsentTarget(
                                now);


        for (EmployeeWorkScheduleDto scheduleDto
                : scheduleList) {


            // 자동 결근 근태 생성
            EmployeeAttendanceDto attendanceDto =
                    EmployeeAttendanceDto.builder()
                            .empAttendanceNo(
                                    employeeAttendanceDao.sequence())
                            .contractNo(
                                    scheduleDto.getContractNo())
                            .workDate(
                                    scheduleDto.getScheduledWorkDate())
                            .clockIn(
                                    null)
                            .clockOut(
                                    null)
                            .breakMinutes(
                                    0.0)
                            .attendanceType(
                                    "absent")
                            .nightHours(
                                    0)
                            .overtimeHours(
                                    0)
                            .build();


            boolean attendanceResult =
                    employeeAttendanceDao.add(
                            attendanceDto);


            if (!attendanceResult) {
                throw new GetOutException();
            }


            // 결근이므로 실제 근무값은 0
            WorkScheduleUpdateRequestVO scheduleUpdateVO =
                    new WorkScheduleUpdateRequestVO();


            scheduleUpdateVO.setWorkScheduleNo(
                    scheduleDto.getWorkScheduleNo());

            scheduleUpdateVO.setActualWorkHours(
                    0.0);

            scheduleUpdateVO.setActualOvertimeHours(
                    0.0);

            scheduleUpdateVO.setActualNightHours(
                    0.0);

            scheduleUpdateVO.setActualHolidayHours(
                    0.0);


            boolean scheduleResult =
                    employeeWorkScheduleDao.update(
                            scheduleUpdateVO);


            if (!scheduleResult) {
                throw new GetOutException();
            }
        }
    }
}