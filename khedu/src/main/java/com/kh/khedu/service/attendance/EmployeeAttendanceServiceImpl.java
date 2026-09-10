package com.kh.khedu.service.attendance;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.dao.payroll.EmployeeWorkScheduleDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;
import com.kh.khedu.dto.payroll.EmployeeWorkScheduleDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToNormalRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceLeaveRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToNormalRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockInResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockOutResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceFindVO;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
public class EmployeeAttendanceServiceImpl
        implements EmployeeAttendanceService {

    private static final double WEEKLY_WORK_LIMIT = 52.0;

    @Autowired
    private EmployeeAttendanceDao employeeAttendanceDao;

    @Autowired
    private ContractDao contractDao;
    
    @Autowired
    private EmployeeWorkScheduleDao employeeWorkScheduleDao;
    
    @Autowired
    private EmployeeDao employeeDao;
    

    @Override
    @Transactional(readOnly = true)
    public boolean working(
            TokenParseResponseVO parseVO) {

        // 로그인 계정이 직원인지 확인
        if (!"직원".equals(
                parseVO.getAccountType())) {

            throw new GetOutException();
        }


        // 로그인 계정 기준 직원정보 조회
        EmployeeDetailVO employeeVO =
                employeeAttendanceDao.findByAccountNo(
                        parseVO.getAccountNo());

        if (employeeVO == null) {
            throw new TargetNotfoundException();
        }


        // 현재 퇴근하지 않은 정상 근태 조회
        Timestamp workDate =
                Timestamp.valueOf(
                        LocalDate.now()
                                .atStartOfDay());

        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                employeeVO.getEmployeeNo())
                        .workDate(
                                workDate)
                        .working(
                                true)
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);


        return attendanceDto != null;
    }
    
    
    @Override
    @Transactional
    public AttendanceClockInResponseVO clockIn(
            TokenParseResponseVO parseVO) {

        // 로그인 계정이 직원인지 확인
        if (!"직원".equals(
                parseVO.getAccountType())) throw new GetOutException();
        


        // 로그인 계정 기준 직원정보 조회
        
        EmployeeDetailVO employeeDetailVO =
                employeeAttendanceDao.findByAccountNo(
                        parseVO.getAccountNo());

        System.out.print(employeeDetailVO);
        if (employeeDetailVO == null) {
            throw new TargetNotfoundException();
        }


        int employeeNo =
                employeeDetailVO.getEmployeeNo();

        
        // 현재 퇴근하지 않은 근태 확인
        
        Timestamp workDate =
                Timestamp.valueOf(
                        LocalDate.now()
                                .atStartOfDay());
        
        AttendanceFindVO workingFindVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                employeeNo)
                        .workDate(
                                workDate)
                        .working(
                                true)
                        .build();
        EmployeeAttendanceDto leftClockOut = employeeAttendanceDao.find(workingFindVO);
        if(leftClockOut!=null) throw new GetOutException();
        
        // 일정 조회용 직원정보
        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(employeeDetailVO.getEmployeeNo())
                        .accountName(employeeDetailVO.getAccountName())
                        .accountId(employeeDetailVO.getAccountId())
                        .build();
        
        

        // 오늘 예정 근무 일정 조회
     
        LocalDateTime now = LocalDateTime.now();
        Timestamp today = Timestamp.valueOf(now.toLocalDate().atStartOfDay());
        
        EmployeeWorkScheduleDto todaySchedule =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        today
                );


        ContractDto contract;


        // =========================
        // 별도 일정이 없는 일반 근무일
        // =========================

        if (todaySchedule == null) {

            // 오늘 적용되는 계약 조회
            Long contractNo =
                    contractDao.findContractByPeriod(
                            employeeNo,
                            today
                    );


            if (contractNo == null) {
                throw new TargetNotfoundException();
            }


            contract =
                    contractDao.find(
                            contractNo
                    );


            if (contract == null) {
                throw new TargetNotfoundException();
            }


            // 실제 출근 가능한 계약인지 확인
            if (!"active".equals(
                    contract.getContractStatus())) {

                throw new GetOutException();
            }


            // 오늘 요일
            String todayDay =
                    now.toLocalDate()
                            .getDayOfWeek()
                            .name();


            // 기본값은 정상 근무일
            String scheduledDayType =
                    "workday";


            // 계약상 주휴일에 실제 출근했다면
            // 휴일근로로 기록
            if (contract.getWeeklyHolidayDay() != null
                    && contract.getWeeklyHolidayDay()
                            .equals(todayDay)) {

                scheduledDayType =
                        "holiday";
            }


         // 자동 스케줄 번호 발급
            long workScheduleNo =
                    employeeWorkScheduleDao.sequence();

            // DAO 등록용 요청 객체
            WorkScheduleAddRequestVO scheduleAddVO =
                    new WorkScheduleAddRequestVO();

            scheduleAddVO.setWorkScheduleNo(workScheduleNo);
            scheduleAddVO.setEmployeeNo(employeeNo);
            scheduleAddVO.setContractNo(contractNo);
            scheduleAddVO.setScheduledWorkDate(today);
            scheduleAddVO.setScheduledClockIn(null);
            scheduleAddVO.setScheduledClockOut(null);
            scheduleAddVO.setScheduledDayType(scheduledDayType);

            // 스케줄 저장
            boolean scheduleResult =
                    employeeWorkScheduleDao.add(scheduleAddVO);

            if (!scheduleResult) {
                throw new GetOutException();
            }

            // 저장된 스케줄을 이후 근태 처리에 사용
            todaySchedule =
                    employeeWorkScheduleDao.findByNo(workScheduleNo);

            if (todaySchedule == null) {
                throw new TargetNotfoundException();
            }
        }


        // =========================
        // 이미 별도 일정이 있는 경우
        // =========================

        else {

            contract =
                    contractDao.find(
                            todaySchedule.getContractNo()
                    );


            if (contract == null) {
                throw new TargetNotfoundException();
            }
        }


        // =========================
        // 명시적 휴무일이면 출근 불가
        // =========================

        if ("dayOff".equals(
                todaySchedule.getScheduledDayType())) {

            throw new GetOutException();
        }


        // =========================
        // 주 52시간 확인
        // =========================

        validateWeeklyWorkLimitBeforeClockIn(
                employeeNo,
                todaySchedule
        );
        


        // 오늘 이미 근태가 생성되었는지 확인
      EmployeeAttendanceDto alreadyClockIn = employeeAttendanceDao.findBySchedule(todaySchedule.getWorkScheduleNo());
      if(alreadyClockIn != null) throw new GetOutException(); 


   // 스케줄의 contractNo를 사용해 정상 근태 생성
      EmployeeAttendanceDto attendanceDto =
              EmployeeAttendanceDto.builder()
                      .empAttendanceNo(
                              employeeAttendanceDao.sequence())
                      .contractNo(
                              todaySchedule.getContractNo())
                      .workDate(
                              today)
                      .clockIn(
                              Timestamp.valueOf(LocalDateTime.now()))
                      .clockOut(
                              null)
                      .breakMinutes(
                            contract.getWrittenBreakMinutes())
                      .attendanceType(
                              "normal")
                      .nightHours(
                              0)
                      .overtimeHours(
                              0)
                      .build();


      // 실제 출근 근태 기록
      boolean result =
              employeeAttendanceDao.add(
                      attendanceDto);

      if (!result) {
          throw new GetOutException();
      }
      

   // 최초 출근이면 고용일자 설정
   employeeDao.updateEmploymentDateIfNull(
           employeeNo,
           attendanceDto.getClockIn()
   );

   // 출근 결과 반환
      return AttendanceClockInResponseVO.builder()
              .empAttendanceNo(
                      attendanceDto.getEmpAttendanceNo())
              .employeeNo(
                      employeeDetailVO.getEmployeeNo())
              .accountName(
                      employeeDetailVO.getAccountName())
              .workDate(
                      attendanceDto.getWorkDate())
              .clockIn(
                      attendanceDto.getClockIn())
              .breakMinutes(
                      attendanceDto.getBreakMinutes())
              .scheduledWorkDayType(
                      todaySchedule.getScheduledDayType())
              .build();
    }
   // 퇴근
    @Override
    @Transactional
    public AttendanceClockOutResponseVO clockOut(
        TokenParseResponseVO parseVO) {

    // 로그인 계정이 직원인지 확인
    if (!"직원".equals(
            parseVO.getAccountType())) {

        throw new GetOutException();
    }


    // 로그인 계정 기준 직원정보 조회
    EmployeeDetailVO employeeDetailVO =
            employeeAttendanceDao.findByAccountNo(
                    parseVO.getAccountNo());

    if (employeeDetailVO == null) {
        throw new TargetNotfoundException();
    }


    // 현재 퇴근하지 않은 근태 조회
    AttendanceFindVO findVO =
            AttendanceFindVO.builder()
                    .employeeNo(
                            employeeDetailVO.getEmployeeNo())
                    .working(
                            true)
                    .build();

    EmployeeAttendanceDto attendanceDto =
            employeeAttendanceDao.find(
                    findVO);

    if (attendanceDto == null) {
        throw new TargetNotfoundException();
    }


    // 일정 조회용 직원정보
    EmployeeSearchByNameVO employeeVO =
            EmployeeSearchByNameVO.builder()
                    .employeeNo(
                            employeeDetailVO.getEmployeeNo())
                    .accountName(
                            employeeDetailVO.getAccountName())
                    .accountId(
                            employeeDetailVO.getAccountId())
                    .build();


    // 실제 근태가 발생한 날짜의 일정 조회
    EmployeeWorkScheduleDto scheduleDto =
            employeeWorkScheduleDao.find(
                    employeeVO,
                    attendanceDto.getWorkDate());

    if (scheduleDto == null) {
        throw new TargetNotfoundException();
    }


    // 계약의 근무시간 조건 조회
    ContractDto contractDto =
            contractDao.findWorkTimeCondition(
                    attendanceDto.getContractNo());

    if (contractDto == null) {
        throw new TargetNotfoundException();
    }


    LocalDateTime clockIn =
            attendanceDto.getClockIn()
                    .toLocalDateTime();

    LocalDateTime clockOut =
            LocalDateTime.now();


    // 퇴근시간 검증
    if (!clockOut.isAfter(
            clockIn)) {

        throw new GetOutException();
    }


 // 출근 ~ 퇴근 전체 체류시간
    long totalWorkMinutes =
            Duration.between(
                    clockIn,
                    clockOut
            ).toMinutes();


    // 계약상 기본 휴게시간
    double defaultBreakMinutes =
            attendanceDto.getBreakMinutes() == null
                    ? 0
                    : attendanceDto.getBreakMinutes();


    // 계약상 하루 소정근로시간
    double dailyWorkMinutes =
            contractDto.getDailyWorkHours()
                    * 60;


    // 정상 근무를 완료하기 위해 필요한 전체 체류시간
    // = 실제 근로시간 + 휴게시간
    double normalStayMinutes =
            dailyWorkMinutes
                    + defaultBreakMinutes;


    double breakMinutes;


    // 정상 근무시간 이상 체류했다면
    // 계약상 기본 휴게시간 적용
    if (totalWorkMinutes >= normalStayMinutes) {

        breakMinutes =
                defaultBreakMinutes;
    }


    // 중도퇴근 / 조퇴
    // 휴게시간 발생 여부를 시스템이 알 수 없으므로
    // 자동 차감하지 않음
    else {

        breakMinutes =
                0;
    }


    // 실제 적용 휴게시간 반영
    attendanceDto.setBreakMinutes(
            breakMinutes
    );


    // 실제 근로시간
    double actualWorkMinutes =
            totalWorkMinutes
                    - breakMinutes;


    double actualWorkHours =
            actualWorkMinutes / 60.0;


    // 연장근무시간 계산
    double standardWorkMinutes =
            contractDto.getDailyWorkHours()
                    * 60;


    double overtimeMinutes =
            actualWorkMinutes
                    - standardWorkMinutes;

    if (overtimeMinutes < 0) {
        overtimeMinutes = 0;
    }


    double actualOvertimeHours =
            overtimeMinutes / 60.0;


    // 야간근무시간 계산
    long nightMinutes = 0;


    LocalDate workDate =
            attendanceDto.getWorkDate()
                    .toLocalDateTime()
                    .toLocalDate();


    // 00:00 ~ 06:00
    LocalDateTime earlyNightStart =
            workDate.atStartOfDay();

    LocalDateTime earlyNightEnd =
            workDate.atTime(
                    6,
                    0);


    LocalDateTime earlyStart =
            clockIn.isAfter(
                    earlyNightStart)
                    ? clockIn
                    : earlyNightStart;

    LocalDateTime earlyEnd =
            clockOut.isBefore(
                    earlyNightEnd)
                    ? clockOut
                    : earlyNightEnd;


    if (earlyEnd.isAfter(
            earlyStart)) {

        nightMinutes +=
                Duration.between(
                        earlyStart,
                        earlyEnd)
                        .toMinutes();
    }


    // 22:00 ~ 익일 06:00
    LocalDateTime lateNightStart =
            workDate.atTime(
                    22,
                    0);

    LocalDateTime lateNightEnd =
            workDate.plusDays(1)
                    .atTime(
                            6,
                            0);


    LocalDateTime lateStart =
            clockIn.isAfter(
                    lateNightStart)
                    ? clockIn
                    : lateNightStart;

    LocalDateTime lateEnd =
            clockOut.isBefore(
                    lateNightEnd)
                    ? clockOut
                    : lateNightEnd;


    if (lateEnd.isAfter(
            lateStart)) {

        nightMinutes +=
                Duration.between(
                        lateStart,
                        lateEnd)
                        .toMinutes();
    }


    double actualNightHours =
            nightMinutes / 60.0;


    // 휴일 / 휴무일 실제 근무시간 계산
    double actualHolidayHours =
            0;


    if ("holiday".equals(
            scheduleDto.getScheduledDayType())
            || "dayOff".equals(
                    scheduleDto.getScheduledDayType())) {

        actualHolidayHours =
                actualWorkHours;
    }

    // =========================
    // 실제 주간 근로시간 확인
    // =========================

    warnWeeklyActualWorkLimit(
         employeeDetailVO.getEmployeeNo(),
         workDate,
         scheduleDto.getWorkScheduleNo(),
         actualWorkHours
    		);

    // 실제 근태 퇴근 반영
    attendanceDto.setClockOut(
            Timestamp.valueOf(
                    clockOut));

    attendanceDto.setNightHours(
            actualNightHours);

    attendanceDto.setOvertimeHours(
            actualOvertimeHours);


    boolean attendanceResult =
            employeeAttendanceDao.update(
                    attendanceDto);

    if (!attendanceResult) {
        throw new GetOutException();
    }


    // 실제 근태 결과를 일정 actual에 반영
    WorkScheduleUpdateRequestVO scheduleUpdateVO =
            WorkScheduleUpdateRequestVO.builder()
                    .workScheduleNo(
                            scheduleDto.getWorkScheduleNo())
                    .actualWorkHours(
                            actualWorkHours)
                    .actualOvertimeHours(
                            actualOvertimeHours)
                    .actualNightHours(
                            actualNightHours)
                    .actualHolidayHours(
                            actualHolidayHours)
                    .build();


    boolean scheduleResult =
            employeeWorkScheduleDao.update(
                    scheduleUpdateVO);

    if (!scheduleResult) {
        throw new GetOutException();
    }


    // 퇴근 결과 반환
    return AttendanceClockOutResponseVO.builder()
            .empAttendanceNo(
                    attendanceDto.getEmpAttendanceNo())
            .clockIn(
                    attendanceDto.getClockIn())
            .clockOut(
                    attendanceDto.getClockOut())
            .breakMinutes(
                    attendanceDto.getBreakMinutes())
            .scheduledWorkDayType(
                    scheduleDto.getScheduledDayType())
            .nightHours(
                    attendanceDto.getNightHours())
            .overtimeHours(
                    attendanceDto.getOvertimeHours())
            .build();
    }
    
 // 정상 근태 -> 정상 근태 수정
    @Override
    @Transactional
    public void normalToNormal(
            AttendanceNormalToNormalRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        // 수정할 근태 조회
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .empAttendanceNo(
                                requestVO.getEmpAttendanceNo())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto == null) {
            throw new TargetNotfoundException();
        }


        // 기존 근태가 정상 근태인지 확인
        if (!"normal".equals(
                attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        // 수정할 출근 / 퇴근시간 확인
        if (requestVO.getClockIn() == null
                || requestVO.getClockOut() == null) {

            throw new GetOutException();
        }


        // 휴게시간 확인
        if (
               requestVO.getBreakMinutes() < 0) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                requestVO.getClockIn()
                        .toLocalDateTime();

        LocalDateTime clockOut =
                requestVO.getClockOut()
                        .toLocalDateTime();


        // 퇴근시간은 출근시간 이후
        if (!clockOut.isAfter(
                clockIn)) {

            throw new GetOutException();
        }


        // 근태에 적용된 계약의 근무시간 조건 조회
        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());

        if (contractDto == null) {
            throw new TargetNotfoundException();
        }


        // 해당 근태와 연결된 근무 일정 조회
        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        // 출근 ~ 퇴근 전체 시간
        long totalWorkMinutes =
                Duration.between(
                        clockIn,
                        clockOut)
                        .toMinutes();


        // 휴게시간 제외 실제 근무시간
        double actualWorkMinutes =
                totalWorkMinutes
                        - requestVO.getBreakMinutes();

        if (actualWorkMinutes < 0) {
            throw new GetOutException();
        }


        double actualWorkHours =
                actualWorkMinutes / 60.0;


        // 계약상 하루 근무시간
        double standardWorkMinutes =
                contractDto.getDailyWorkHours()
                        * 60;


        // 연장근무시간 계산
        double overtimeMinutes =
                actualWorkMinutes
                        - standardWorkMinutes;

        if (overtimeMinutes < 0) {
            overtimeMinutes = 0;
        }


        double actualOvertimeHours =
                overtimeMinutes / 60.0;

        

        // 야간근무시간 계산
        long nightMinutes = 0;


        LocalDate workDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // 00:00 ~ 06:00
        LocalDateTime earlyNightStart =
                workDate.atStartOfDay();

        LocalDateTime earlyNightEnd =
                workDate.atTime(
                        6,
                        0);


        LocalDateTime earlyStart =
                clockIn.isAfter(
                        earlyNightStart)
                        ? clockIn
                        : earlyNightStart;

        LocalDateTime earlyEnd =
                clockOut.isBefore(
                        earlyNightEnd)
                        ? clockOut
                        : earlyNightEnd;


        if (earlyEnd.isAfter(
                earlyStart)) {

            nightMinutes +=
                    Duration.between(
                            earlyStart,
                            earlyEnd)
                            .toMinutes();
        }


        // 22:00 ~ 익일 06:00
        LocalDateTime lateNightStart =
                workDate.atTime(
                        22,
                        0);

        LocalDateTime lateNightEnd =
                workDate.plusDays(1)
                        .atTime(
                                6,
                                0);


        LocalDateTime lateStart =
                clockIn.isAfter(
                        lateNightStart)
                        ? clockIn
                        : lateNightStart;

        LocalDateTime lateEnd =
                clockOut.isBefore(
                        lateNightEnd)
                        ? clockOut
                        : lateNightEnd;


        if (lateEnd.isAfter(
                lateStart)) {

            nightMinutes +=
                    Duration.between(
                            lateStart,
                            lateEnd)
                            .toMinutes();
        }


        double actualNightHours =
                nightMinutes / 60.0;


        // 휴일 / 휴무일 실제 근무시간 계산
        double actualHolidayHours = 0;


        if ("holiday".equals(
                scheduleDto.getScheduledDayType())
                || "dayOff".equals(
                        scheduleDto.getScheduledDayType())) {

            actualHolidayHours =
                    actualWorkHours;
        }


        // 실제 근태 수정
        attendanceDto.setClockIn(
                requestVO.getClockIn());

        attendanceDto.setClockOut(
                requestVO.getClockOut());

        attendanceDto.setBreakMinutes(
                requestVO.getBreakMinutes());

        attendanceDto.setAttendanceType(
                "normal");

        attendanceDto.setNightHours(
                actualNightHours);

        attendanceDto.setOvertimeHours(
                actualOvertimeHours);


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 수정된 실제 근태 결과를 근무 일정에 반영
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                actualWorkHours)
                        .actualOvertimeHours(
                                actualOvertimeHours)
                        .actualNightHours(
                                actualNightHours)
                        .actualHolidayHours(
                                actualHolidayHours)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
 // 정상 근태 -> 비근무 근태 수정
    @Override
    @Transactional
    public void normalToAbsent(
            AttendanceNormalToAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        // 수정할 근태 조회
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .empAttendanceNo(
                                requestVO.getEmpAttendanceNo())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto == null) {
            throw new TargetNotfoundException();
        }


        // 기존 근태가 정상 근태인지 확인
        if (!"normal".equals(
                attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        // 변경할 비근무 유형 확인
        if (!"absent".equals(
                requestVO.getAttendanceType())
                && !"paid_leave".equals(
                        requestVO.getAttendanceType())
                && !"unpaid_leave".equals(
                        requestVO.getAttendanceType())) {

            throw new GetOutException();
        }


        // 해당 근태와 연결된 근무 일정 조회
        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        // 정상 근태 -> 비근무 근태 변경
        attendanceDto.setAttendanceType(
                requestVO.getAttendanceType());


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 실제 근무가 사라졌으므로
        // 스케줄 actual 4종 초기화
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                0.0)
                        .actualOvertimeHours(
                                0.0)
                        .actualNightHours(
                                0.0)
                        .actualHolidayHours(
                                0.0)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
    
 // 비근무 근태 -> 정상 근태 수정
    @Override
    @Transactional
    public void absentToNormal(
            AttendanceAbsentToNormalRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        // 수정할 근태 조회
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .empAttendanceNo(
                                requestVO.getEmpAttendanceNo())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto == null) {
            throw new TargetNotfoundException();
        }


        // 기존 근태가 비근무 상태인지 확인
        if (!"absent".equals(
                attendanceDto.getAttendanceType())
                && !"paid_leave".equals(
                        attendanceDto.getAttendanceType())
                && !"unpaid_leave".equals(
                        attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        // 출근 / 퇴근시간 확인
        if (requestVO.getClockIn() == null
                || requestVO.getClockOut() == null) {

            throw new GetOutException();
        }


        // 휴게시간 확인
        if (requestVO.getBreakMinutes() < 0) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                requestVO.getClockIn()
                        .toLocalDateTime();

        LocalDateTime clockOut =
                requestVO.getClockOut()
                        .toLocalDateTime();


        // 퇴근시간은 출근시간 이후
        if (!clockOut.isAfter(
                clockIn)) {

            throw new GetOutException();
        }


        // 계약 근무시간 조건 조회
        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());

        if (contractDto == null) {
            throw new TargetNotfoundException();
        }


        // 해당 근태와 연결된 근무 일정 조회
        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        // 출근 ~ 퇴근 전체시간
        long totalWorkMinutes =
                Duration.between(
                        clockIn,
                        clockOut)
                        .toMinutes();


        // 휴게시간 제외 실제 근무시간
        double actualWorkMinutes =
                totalWorkMinutes
                        - requestVO.getBreakMinutes();

        if (actualWorkMinutes < 0) {
            throw new GetOutException();
        }


        double actualWorkHours =
                actualWorkMinutes / 60.0;


        // 계약상 하루 근무시간
        double standardWorkMinutes =
                contractDto.getDailyWorkHours()
                        * 60;


        // 연장근무시간 계산
        double overtimeMinutes =
                actualWorkMinutes
                        - standardWorkMinutes;

        if (overtimeMinutes < 0) {
            overtimeMinutes = 0;
        }


        double actualOvertimeHours =
                overtimeMinutes / 60.0;


        // 야간근무시간 계산
        long nightMinutes = 0;


        LocalDate workDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // 00:00 ~ 06:00
        LocalDateTime earlyNightStart =
                workDate.atStartOfDay();

        LocalDateTime earlyNightEnd =
                workDate.atTime(
                        6,
                        0);


        LocalDateTime earlyStart =
                clockIn.isAfter(
                        earlyNightStart)
                        ? clockIn
                        : earlyNightStart;

        LocalDateTime earlyEnd =
                clockOut.isBefore(
                        earlyNightEnd)
                        ? clockOut
                        : earlyNightEnd;


        if (earlyEnd.isAfter(
                earlyStart)) {

            nightMinutes +=
                    Duration.between(
                            earlyStart,
                            earlyEnd)
                            .toMinutes();
        }


        // 22:00 ~ 익일 06:00
        LocalDateTime lateNightStart =
                workDate.atTime(
                        22,
                        0);

        LocalDateTime lateNightEnd =
                workDate.plusDays(1)
                        .atTime(
                                6,
                                0);


        LocalDateTime lateStart =
                clockIn.isAfter(
                        lateNightStart)
                        ? clockIn
                        : lateNightStart;

        LocalDateTime lateEnd =
                clockOut.isBefore(
                        lateNightEnd)
                        ? clockOut
                        : lateNightEnd;


        if (lateEnd.isAfter(
                lateStart)) {

            nightMinutes +=
                    Duration.between(
                            lateStart,
                            lateEnd)
                            .toMinutes();
        }


        double actualNightHours =
                nightMinutes / 60.0;


        // 휴일 / 휴무일 실제 근무시간 계산
        double actualHolidayHours = 0;


        if ("holiday".equals(
                scheduleDto.getScheduledDayType())
                || "dayOff".equals(
                        scheduleDto.getScheduledDayType())) {

            actualHolidayHours =
                    actualWorkHours;
        }


        // 비근무 -> 정상 근태 변경
        attendanceDto.setClockIn(
                requestVO.getClockIn());

        attendanceDto.setClockOut(
                requestVO.getClockOut());

        attendanceDto.setBreakMinutes(
                requestVO.getBreakMinutes());

        attendanceDto.setAttendanceType(
                "normal");

        attendanceDto.setNightHours(
                actualNightHours);

        attendanceDto.setOvertimeHours(
                actualOvertimeHours);


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 실제 근태 결과를 스케줄 actual에 반영
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                actualWorkHours)
                        .actualOvertimeHours(
                                actualOvertimeHours)
                        .actualNightHours(
                                actualNightHours)
                        .actualHolidayHours(
                                actualHolidayHours)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
    
 // 비근무 근태 -> 비근무 근태 수정
    @Override
    @Transactional
    public void absentToAbsent(
            AttendanceAbsentToAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        // 수정할 근태 조회
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .empAttendanceNo(
                                requestVO.getEmpAttendanceNo())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto == null) {
            throw new TargetNotfoundException();
        }


        // 기존 근태가 비근무 상태인지 확인
        if (!"absent".equals(
                attendanceDto.getAttendanceType())
                && !"paid_leave".equals(
                        attendanceDto.getAttendanceType())
                && !"unpaid_leave".equals(
                        attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        // 변경할 근태도 비근무 상태인지 확인
        if (!"absent".equals(
                requestVO.getAttendanceType())
                && !"paid_leave".equals(
                        requestVO.getAttendanceType())
                && !"unpaid_leave".equals(
                        requestVO.getAttendanceType())) {

            throw new GetOutException();
        }


        // 해당 근태와 연결된 근무 일정 조회
        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        // 비근무 유형 변경
        attendanceDto.setAttendanceType(
                requestVO.getAttendanceType());


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 비근무 상태이므로 actual 4종은 0 유지
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                0.0)
                        .actualOvertimeHours(
                                0.0)
                        .actualNightHours(
                                0.0)
                        .actualHolidayHours(
                                0.0)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
 // 결근 등록
    @Override
    @Transactional
    public void absent(
            AttendanceAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {

    	if (requestVO == null || requestVO.getWorkDate() == null) {
    	    throw new GetOutException();
    	}

    	requestVO.setWorkDate(
    	        Timestamp.valueOf(
    	                requestVO.getWorkDate()
    	                        .toLocalDateTime()
    	                        .toLocalDate()
    	                        .atStartOfDay()
    	        )
    	);
    	
    	
        // 일정 조회용 직원정보
        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .build();


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        requestVO.getWorkDate()
                );


        // =========================
        // 등록된 일정이 없는 경우
        // 관리자 판단으로 workday 생성
        // =========================

        if (scheduleDto == null) {

            Long contractNo =
                    contractDao.findContractByPeriod(
                            requestVO.getEmployeeNo(),
                            requestVO.getWorkDate()
                    );


            if (contractNo == null) {
                throw new TargetNotfoundException();
            }


            ContractDto contract =
                    contractDao.find(
                            contractNo
                    );


            if (contract == null) {
                throw new TargetNotfoundException();
            }


            LocalDate absentDate =
                    requestVO.getWorkDate()
                            .toLocalDateTime()
                            .toLocalDate();


            String absentDay =
                    absentDate
                            .getDayOfWeek()
                            .name();


            // 계약상 주휴일에는
            // 결근을 생성할 수 없음
            if (contract.getWeeklyHolidayDay() != null
                    && contract.getWeeklyHolidayDay()
                            .equals(absentDay)) {

                throw new GetOutException();
            }

         // 자동 스케줄 번호 발급
            long workScheduleNo =
                    employeeWorkScheduleDao.sequence();

            // DAO 등록용 요청 객체
            WorkScheduleAddRequestVO scheduleAddVO =
                    new WorkScheduleAddRequestVO();

            scheduleAddVO.setWorkScheduleNo(workScheduleNo);
            scheduleAddVO.setEmployeeNo(requestVO.getEmployeeNo());
            scheduleAddVO.setContractNo(contractNo);
            scheduleAddVO.setScheduledWorkDate(requestVO.getWorkDate());
            scheduleAddVO.setScheduledClockIn(null);
            scheduleAddVO.setScheduledClockOut(null);
            scheduleAddVO.setScheduledDayType("workday");

            // 스케줄 저장
            boolean scheduleResult =
                    employeeWorkScheduleDao.add(scheduleAddVO);

            if (!scheduleResult) {
                throw new GetOutException();
            }

            // 저장된 스케줄을 이후 결근 처리에 사용
            scheduleDto =
                    employeeWorkScheduleDao.findByNo(workScheduleNo);

            if (scheduleDto == null) {
                throw new TargetNotfoundException();
            }
        
        }


        // =========================
        // 휴일 / 휴무일은 결근 불가
        // =========================

        if (!"workday".equals(
                scheduleDto.getScheduledDayType())) {

            throw new GetOutException();
        }


        // 해당 날짜에 이미 근태가 있는지 확인
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .workDate(
                                requestVO.getWorkDate())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto != null) {
            throw new GetOutException();
        }


        // 결근 근태 생성
        EmployeeAttendanceDto absentDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                scheduleDto.getContractNo())
                        .workDate(
                                requestVO.getWorkDate())
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
                        absentDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 실제 근무가 없으므로 Schedule actual 4종 0
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                0.0)
                        .actualOvertimeHours(
                                0.0)
                        .actualNightHours(
                                0.0)
                        .actualHolidayHours(
                                0.0)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
    
 // 유급휴가 등록
    @Override
    @Transactional
    public void paidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        // 일정 조회용 직원정보
        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .build();


        // 해당 날짜 근무 일정 조회
        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        requestVO.getWorkDate());

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        // 해당 날짜에 이미 근태가 있는지 확인
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .workDate(
                                requestVO.getWorkDate())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto != null) {
            throw new GetOutException();
        }


        // 유급휴가 근태 생성
        EmployeeAttendanceDto leaveDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                scheduleDto.getContractNo())
                        .workDate(
                                requestVO.getWorkDate())
                        .clockIn(
                                null)
                        .clockOut(
                                null)
                        .breakMinutes(
                                0.0)
                        .attendanceType(
                                "paid_leave")
                        .nightHours(
                                0)
                        .overtimeHours(
                                0)
                        .build();


        boolean attendanceResult =
                employeeAttendanceDao.add(
                        leaveDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 실제 근무가 없으므로 Schedule actual 4종 0
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                0.0)
                        .actualOvertimeHours(
                                0.0)
                        .actualNightHours(
                                0.0)
                        .actualHolidayHours(
                                0.0)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
 // 무급휴가 등록
    @Override
    @Transactional
    public void unpaidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        // 일정 조회용 직원정보
        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .build();


        // 해당 날짜 근무 일정 조회
        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        requestVO.getWorkDate());

        if (scheduleDto == null) {
            throw new TargetNotfoundException();
        }


        // 해당 날짜에 이미 근태가 있는지 확인
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .workDate(
                                requestVO.getWorkDate())
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);

        if (attendanceDto != null) {
            throw new GetOutException();
        }


        // 무급휴가 근태 생성
        EmployeeAttendanceDto leaveDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                scheduleDto.getContractNo())
                        .workDate(
                                requestVO.getWorkDate())
                        .clockIn(
                                null)
                        .clockOut(
                                null)
                        .breakMinutes(
                                0.0)
                        .attendanceType(
                                "unpaid_leave")
                        .nightHours(
                                0)
                        .overtimeHours(
                                0)
                        .build();


        boolean attendanceResult =
                employeeAttendanceDao.add(
                        leaveDto);

        if (!attendanceResult) {
            throw new GetOutException();
        }


        // 실제 근무가 없으므로 Schedule actual 4종 0
        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                0.0)
                        .actualOvertimeHours(
                                0.0)
                        .actualNightHours(
                                0.0)
                        .actualHolidayHours(
                                0.0)
                        .build();


        boolean scheduleResult =
                employeeWorkScheduleDao.update(
                        scheduleUpdateVO);

        if (!scheduleResult) {
            throw new GetOutException();
        }
    }
    
    
 
// =====================================================
// 출근 전 주 52시간 검증
//
// KH EDU 단위기간:
// 월요일 00:00 ~ 다음 월요일 00:00 미만
// =====================================================
private void validateWeeklyWorkLimitBeforeClockIn(
        int employeeNo,
        EmployeeWorkScheduleDto todaySchedule) {


    LocalDate workDate =
            todaySchedule
                    .getScheduledWorkDate()
                    .toLocalDateTime()
                    .toLocalDate();


    // 현재까지 실제 근로시간
    double weeklyActualWorkHours =
            findWeeklyActualWorkHours(
                    employeeNo,
                    workDate,
                    todaySchedule.getWorkScheduleNo()
            );


    // 이미 52시간을 채웠다면
    // 추가 출근 자체 불가
    if (weeklyActualWorkHours
            >= WEEKLY_WORK_LIMIT) {

        throw new GetOutException();
    }


    // 오늘 스케줄에 적용되는 계약
    ContractDto contractDto =
            contractDao.find(
                    todaySchedule.getContractNo()
            );


    if (contractDto == null) {

        throw new TargetNotfoundException();
    }


    // =========================
    // 오늘 예상 근로시간
    // =========================

    double todayScheduledWorkHours;


    // 예정 출퇴근시간이 둘 다 없는 경우
    // 자동 생성된 기본 workday
    if (todaySchedule.getScheduledClockIn() == null
            && todaySchedule.getScheduledClockOut() == null) {


        todayScheduledWorkHours =
                contractDto.getDailyWorkHours();
    }


    // 하나만 null이면 잘못된 스케줄
    else if (todaySchedule.getScheduledClockIn() == null
            || todaySchedule.getScheduledClockOut() == null) {

        throw new GetOutException();
    }


    // 예정시간이 모두 존재하는
    // 관리자가 직접 등록한 스케줄
    else {

        LocalDateTime scheduledClockIn =
                todaySchedule
                        .getScheduledClockIn()
                        .toLocalDateTime();


        LocalDateTime scheduledClockOut =
                todaySchedule
                        .getScheduledClockOut()
                        .toLocalDateTime();


        if (!scheduledClockOut.isAfter(
                scheduledClockIn)) {

            throw new GetOutException();
        }


        double breakMinutes =
                contractDto.getWrittenBreakMinutes() == null
                        ? 0
                        : contractDto.getWrittenBreakMinutes();


        double scheduledWorkMinutes =
                Duration.between(
                        scheduledClockIn,
                        scheduledClockOut)
                        .toMinutes()
                - breakMinutes;


        if (scheduledWorkMinutes < 0) {

            throw new GetOutException();
        }


        todayScheduledWorkHours =
                scheduledWorkMinutes / 60.0;
    }


    // 현재 실제 누적 +
    // 오늘 예상근무를 하면 52시간 초과
    if (weeklyActualWorkHours
            + todayScheduledWorkHours
            > WEEKLY_WORK_LIMIT) {

        throw new GetOutException();
    }
}


 // =====================================================
 // 주간 실제 근로시간 조회
 //
 // 현재 수정/퇴근 처리 대상 스케줄은 제외 가능
 // =====================================================
 private double findWeeklyActualWorkHours(
         int employeeNo,
         LocalDate targetDate,
         Long excludeWorkScheduleNo) {


     LocalDate weekStart =
             targetDate.with(
                     TemporalAdjusters.previousOrSame(
                             DayOfWeek.MONDAY
                     )
             );


     LocalDate weekEnd =
             weekStart.plusDays(7);


     Timestamp weekStartDate =
             Timestamp.valueOf(
                     weekStart.atStartOfDay()
             );


     Timestamp weekEndDate =
             Timestamp.valueOf(
                     weekEnd.atStartOfDay()
             );


     List<EmployeeWorkScheduleDto> weeklyScheduleList =
             employeeWorkScheduleDao.findByPeriod(
                     employeeNo,
                     weekStartDate,
                     weekEndDate
             );


     double weeklyActualWorkHours = 0;


     for (EmployeeWorkScheduleDto scheduleDto
             : weeklyScheduleList) {


         // 현재 처리 중인 일정은 제외
         // 이후 새 actualWorkHours를 직접 더하기 위함
         if (excludeWorkScheduleNo != null
                 && scheduleDto.getWorkScheduleNo()
                         == excludeWorkScheduleNo.longValue()) {

             continue;
         }


         if (scheduleDto.getActualWorkHours()
                 == null) {

             continue;
         }


         weeklyActualWorkHours +=
                 scheduleDto.getActualWorkHours();
     }


     return weeklyActualWorkHours;
 }


 // =====================================================
 // 실제 근로 결과 52시간 초과 확인
 //
 // 실제로 발생한 근로는 삭제하거나 저장 거부하지 않음
 // =====================================================
 private void warnWeeklyActualWorkLimit(
         int employeeNo,
         LocalDate targetDate,
         long workScheduleNo,
         double currentActualWorkHours) {


     double otherActualWorkHours =
             findWeeklyActualWorkHours(
                     employeeNo,
                     targetDate,
                     workScheduleNo
             );


     double weeklyActualWorkHours =
             otherActualWorkHours
             + currentActualWorkHours;


     if (weeklyActualWorkHours
             > WEEKLY_WORK_LIMIT) {

         log.warn(
                 "주 52시간 초과 - employeeNo={}, workDate={}, weeklyActualWorkHours={}",
                 employeeNo,
                 targetDate,
                 weeklyActualWorkHours
         );
     }
 }
    
}