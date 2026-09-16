package com.kh.khedu.service.attendance;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
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
import com.kh.khedu.error.AttendanceTargetChecker;
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

    @Autowired
    private AttendanceTargetChecker attendanceTargetChecker;



    // =====================================================
    // 현재 근무 중 여부
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public boolean working(
            TokenParseResponseVO parseVO) {


        if (!"직원".equals(
                parseVO.getAccountType())) {

            throw new GetOutException();
        }


        EmployeeDetailVO employeeVO =
                employeeAttendanceDao.findByAccountNo(
                        parseVO.getAccountNo());

        if (employeeVO == null) {

            throw new TargetNotfoundException();
        }


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



    // =====================================================
    // 출근
    // =====================================================

    @Override
    @Transactional
    public AttendanceClockInResponseVO clockIn(
            TokenParseResponseVO parseVO) {


        if (!"직원".equals(
                parseVO.getAccountType())) {

            throw new GetOutException();
        }


        attendanceTargetChecker.check(
                parseVO
        );


        EmployeeDetailVO employeeDetailVO =
                employeeAttendanceDao.findByAccountNo(
                        parseVO.getAccountNo());


        if (employeeDetailVO == null) {

            throw new TargetNotfoundException();
        }


        int employeeNo =
                employeeDetailVO.getEmployeeNo();


        Timestamp workDate =
                Timestamp.valueOf(
                        LocalDate.now()
                                .atStartOfDay());


        // =========================
        // 미퇴근 근태 확인
        // =========================

        AttendanceFindVO workingFindVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                employeeNo)
                        .workDate(
                                workDate)
                        .working(
                                true)
                        .build();


        EmployeeAttendanceDto leftClockOut =
                employeeAttendanceDao.find(
                        workingFindVO);


        if (leftClockOut != null) {

            throw new GetOutException();
        }


        // =========================
        // 일정 조회용 직원정보
        // =========================

        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                employeeDetailVO.getEmployeeNo())
                        .accountName(
                                employeeDetailVO.getAccountName())
                        .accountId(
                                employeeDetailVO.getAccountId())
                        .build();


        LocalDateTime now =
                LocalDateTime.now();


        Timestamp today =
                Timestamp.valueOf(
                        now.toLocalDate()
                                .atStartOfDay());


        EmployeeWorkScheduleDto todaySchedule =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        today);


        ContractDto contract;


        // =========================
        // 별도 일정이 없는 일반 근무일
        // =========================

        if (todaySchedule == null) {


            Long contractNo =
                    contractDao.findContractByPeriod(
                            employeeNo,
                            today);


            if (contractNo == null) {

                throw new TargetNotfoundException();
            }


            contract =
                    contractDao.find(
                            contractNo);


            if (contract == null) {

                throw new TargetNotfoundException();
            }


            if (!"active".equals(
                    contract.getContractStatus())) {

                throw new GetOutException();
            }


            String todayDay =
                    now.toLocalDate()
                            .getDayOfWeek()
                            .name();


            String scheduledDayType =
                    "workday";


            // 계약상 주휴일 실제 출근
            if (contract.getWeeklyHolidayDay() != null
                    && contract.getWeeklyHolidayDay()
                            .equals(todayDay)) {

                scheduledDayType =
                        "holiday";
            }


            long workScheduleNo =
                    employeeWorkScheduleDao.sequence();


            WorkScheduleAddRequestVO scheduleAddVO =
                    new WorkScheduleAddRequestVO();


            scheduleAddVO.setWorkScheduleNo(
                    workScheduleNo);

            scheduleAddVO.setEmployeeNo(
                    employeeNo);

            scheduleAddVO.setContractNo(
                    contractNo);

            scheduleAddVO.setScheduledWorkDate(
                    today);

            scheduleAddVO.setScheduledClockIn(
                    null);

            scheduleAddVO.setScheduledClockOut(
                    null);

            scheduleAddVO.setScheduledDayType(
                    scheduledDayType);


            boolean scheduleResult =
                    employeeWorkScheduleDao.add(
                            scheduleAddVO);


            if (!scheduleResult) {

                throw new GetOutException();
            }


            todaySchedule =
                    employeeWorkScheduleDao.findByNo(
                            workScheduleNo);


            if (todaySchedule == null) {

                throw new TargetNotfoundException();
            }
        }


        // =========================
        // 이미 일정 존재
        // =========================

        else {


            contract =
                    contractDao.find(
                            todaySchedule.getContractNo());


            if (contract == null) {

                throw new TargetNotfoundException();
            }
        }


        // =========================
        // 출근 전 주 52시간 확인
        // =========================

        validateWeeklyWorkLimitBeforeClockIn(
                employeeNo,
                todaySchedule);


        // =========================
        // 이미 오늘 근태 존재 확인
        // =========================

        EmployeeAttendanceDto alreadyClockIn =
                employeeAttendanceDao.findBySchedule(
                        todaySchedule.getWorkScheduleNo());


        if (alreadyClockIn != null) {

            throw new GetOutException();
        }


        // =========================
        // 정상근태 생성
        // =========================

        EmployeeAttendanceDto attendanceDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                todaySchedule.getContractNo())
                        .workDate(
                                today)
                        .clockIn(
                                Timestamp.valueOf(
                                        LocalDateTime.now()))
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



    // =====================================================
    // 퇴근
    // =====================================================

    @Override
    @Transactional
    public AttendanceClockOutResponseVO clockOut(
            TokenParseResponseVO parseVO) {


        if (!"직원".equals(
                parseVO.getAccountType())) {

            throw new GetOutException();
        }


        attendanceTargetChecker.check(
                parseVO
        );


        EmployeeDetailVO employeeDetailVO =
                employeeAttendanceDao.findByAccountNo(
                        parseVO.getAccountNo());


        if (employeeDetailVO == null) {

            throw new TargetNotfoundException();
        }


        int employeeNo =
                employeeDetailVO.getEmployeeNo();


        // =========================
        // 미퇴근 정상근태 조회
        // =========================

        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                employeeNo)
                        .working(
                                true)
                        .build();


        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);


        if (attendanceDto == null) {

            throw new TargetNotfoundException();
        }


        // =========================
        // 연결 일정 조회
        // =========================

        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                employeeNo)
                        .accountName(
                                employeeDetailVO.getAccountName())
                        .accountId(
                                employeeDetailVO.getAccountId())
                        .build();


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        attendanceDto.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


        // =========================
        // 계약 근무시간 조건
        // =========================

        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());


        if (contractDto == null) {

            throw new TargetNotfoundException();
        }


        if (contractDto.getDailyWorkHours() == null
                || contractDto.getDailyWorkHours() <= 0) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                attendanceDto.getClockIn()
                        .toLocalDateTime();


        LocalDateTime clockOut =
                LocalDateTime.now();


        if (!clockOut.isAfter(
                clockIn)) {

            throw new GetOutException();
        }


        // =========================
        // 전체 체류시간
        // =========================

        long totalWorkMinutes =
                Duration.between(
                        clockIn,
                        clockOut)
                        .toMinutes();


        double defaultBreakMinutes =
                attendanceDto.getBreakMinutes() == null
                        ? 0
                        : attendanceDto.getBreakMinutes();


        double dailyWorkMinutes =
                contractDto.getDailyWorkHours()
                        * 60;


        double normalStayMinutes =
                dailyWorkMinutes
                + defaultBreakMinutes;


        double breakMinutes;


        if (totalWorkMinutes
                >= normalStayMinutes) {

            breakMinutes =
                    defaultBreakMinutes;
        }

        else {

            breakMinutes =
                    0;
        }


        attendanceDto.setBreakMinutes(
                breakMinutes);


        // =========================
        // 실제 근로시간
        // =========================

        double actualWorkMinutes =
                totalWorkMinutes
                - breakMinutes;


        if (actualWorkMinutes < 0) {

            throw new GetOutException();
        }


        double actualWorkHours =
                actualWorkMinutes / 60.0;


        // =========================
        // 야간근로시간
        // =========================

        LocalDate workDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        long nightMinutes =
                calculateNightMinutes(
                        workDate,
                        clockIn,
                        clockOut);


        double actualNightHours =
                nightMinutes / 60.0;


        // =========================
        // 휴일근로시간
        // =========================

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
        // 실제 52시간 초과는 경고
        // 실제로 일한 시간은 버리면 안 됨
        // =========================

        warnWeeklyActualWorkLimit(
                employeeNo,
                workDate,
                scheduleDto.getWorkScheduleNo(),
                actualWorkHours
        );


        // =========================
        // Attendance 퇴근 반영
        //
        // overtime은 아래 주 전체 재계산에서
        // 최종 확정
        // =========================

        attendanceDto.setClockOut(
                Timestamp.valueOf(
                        clockOut));

        attendanceDto.setNightHours(
                actualNightHours);

        attendanceDto.setOvertimeHours(
                0);


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);


        if (!attendanceResult) {

            throw new GetOutException();
        }


        // =========================
        // Schedule actual 반영
        // =========================

        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                actualWorkHours)
                        .actualOvertimeHours(
                                0.0)
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


        // =========================
        // 일 소정 + 주 소정 기준
        // 이번 주 전체 연장근로 재계산
        // =========================

        recalculateWeeklyOvertime(
                employeeNo,
                workDate);


        // 재계산된 근태 다시 조회
        AttendanceFindVO updatedFindVO =
                AttendanceFindVO.builder()
                        .empAttendanceNo(
                                attendanceDto.getEmpAttendanceNo())
                        .build();


        EmployeeAttendanceDto updatedAttendance =
                employeeAttendanceDao.find(
                        updatedFindVO);


        if (updatedAttendance == null) {

            throw new TargetNotfoundException();
        }


        return AttendanceClockOutResponseVO.builder()
                .empAttendanceNo(
                        updatedAttendance.getEmpAttendanceNo())
                .clockIn(
                        updatedAttendance.getClockIn())
                .clockOut(
                        updatedAttendance.getClockOut())
                .breakMinutes(
                        updatedAttendance.getBreakMinutes())
                .scheduledWorkDayType(
                        scheduleDto.getScheduledDayType())
                .nightHours(
                        updatedAttendance.getNightHours())
                .overtimeHours(
                        updatedAttendance.getOvertimeHours())
                .build();
    }



    // =====================================================
    // 정상 -> 정상
    // =====================================================

    @Override
    @Transactional
    public void normalToNormal(
            AttendanceNormalToNormalRequestVO requestVO,
            TokenParseResponseVO parseVO) {


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


        if (!"normal".equals(
                attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (requestVO.getClockIn() == null
                || requestVO.getClockOut() == null) {

            throw new GetOutException();
        }


        if (requestVO.getBreakMinutes() == null
                || requestVO.getBreakMinutes() < 0) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                requestVO.getClockIn()
                        .toLocalDateTime();


        LocalDateTime clockOut =
                requestVO.getClockOut()
                        .toLocalDateTime();


        if (!clockOut.isAfter(
                clockIn)) {

            throw new GetOutException();
        }


        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());


        if (contractDto == null) {

            throw new TargetNotfoundException();
        }


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


        // =========================
        // 실제 근로시간
        // =========================

        long totalWorkMinutes =
                Duration.between(
                        clockIn,
                        clockOut)
                        .toMinutes();


        double actualWorkMinutes =
                totalWorkMinutes
                - requestVO.getBreakMinutes();


        if (actualWorkMinutes < 0) {

            throw new GetOutException();
        }


        double actualWorkHours =
                actualWorkMinutes / 60.0;


        LocalDate workDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // =========================
        // 야간근로
        // =========================

        long nightMinutes =
                calculateNightMinutes(
                        workDate,
                        clockIn,
                        clockOut);


        double actualNightHours =
                nightMinutes / 60.0;


        // =========================
        // 휴일근로
        // =========================

        double actualHolidayHours =
                0;


        if ("holiday".equals(
                scheduleDto.getScheduledDayType())
                || "dayOff".equals(
                        scheduleDto.getScheduledDayType())) {

            actualHolidayHours =
                    actualWorkHours;
        }


        // 실제 52시간 초과 경고
        int employeeNo =
                contractDao.find(
                        attendanceDto.getContractNo())
                        .getEmployeeNo();


        warnWeeklyActualWorkLimit(
                employeeNo,
                workDate,
                scheduleDto.getWorkScheduleNo(),
                actualWorkHours
        );


        // =========================
        // Attendance 수정
        // =========================

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

        // 주 전체 재계산 전에 임시 0
        attendanceDto.setOvertimeHours(
                0);


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);


        if (!attendanceResult) {

            throw new GetOutException();
        }


        // =========================
        // Schedule 수정
        // =========================

        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                actualWorkHours)
                        .actualOvertimeHours(
                                0.0)
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


        // 주 전체 연장시간 재계산
        recalculateWeeklyOvertime(
                employeeNo,
                workDate);
    }



    // =====================================================
    // 정상 -> 비근무
    // =====================================================

    @Override
    @Transactional
    public void normalToAbsent(
            AttendanceNormalToAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {


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


        if (!"normal".equals(
                attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (!"absent".equals(
                requestVO.getAttendanceType())
                && !"paid_leave".equals(
                        requestVO.getAttendanceType())
                && !"unpaid_leave".equals(
                        requestVO.getAttendanceType())) {

            throw new GetOutException();
        }


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


        ContractDto contractDto =
                contractDao.find(
                        attendanceDto.getContractNo());


        if (contractDto == null) {

            throw new TargetNotfoundException();
        }


        int employeeNo =
                contractDto.getEmployeeNo();


        LocalDate workDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // =========================
        // 정상 -> 비근무
        // =========================

        attendanceDto.setAttendanceType(
                requestVO.getAttendanceType());

        attendanceDto.setClockIn(
                null);

        attendanceDto.setClockOut(
                null);

        attendanceDto.setBreakMinutes(
                0.0);

        attendanceDto.setNightHours(
                0);

        attendanceDto.setOvertimeHours(
                0);


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);


        if (!attendanceResult) {

            throw new GetOutException();
        }


        // =========================
        // 실제근로 제거
        // =========================

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


        // 앞 날짜 근무가 사라지면
        // 이후 날짜의 주 소정 초과시간도 달라질 수 있음
        recalculateWeeklyOvertime(
                employeeNo,
                workDate);
    }



    // =====================================================
    // 비근무 -> 정상
    // =====================================================

    @Override
    @Transactional
    public void absentToNormal(
            AttendanceAbsentToNormalRequestVO requestVO,
            TokenParseResponseVO parseVO) {


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


        if (!"absent".equals(
                attendanceDto.getAttendanceType())
                && !"paid_leave".equals(
                        attendanceDto.getAttendanceType())
                && !"unpaid_leave".equals(
                        attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (requestVO.getClockIn() == null
                || requestVO.getClockOut() == null) {

            throw new GetOutException();
        }


        if (requestVO.getBreakMinutes() == null
                || requestVO.getBreakMinutes() < 0) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                requestVO.getClockIn()
                        .toLocalDateTime();


        LocalDateTime clockOut =
                requestVO.getClockOut()
                        .toLocalDateTime();


        if (!clockOut.isAfter(
                clockIn)) {

            throw new GetOutException();
        }


        ContractDto contractDto =
                contractDao.find(
                        attendanceDto.getContractNo());


        if (contractDto == null) {

            throw new TargetNotfoundException();
        }


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


        long totalWorkMinutes =
                Duration.between(
                        clockIn,
                        clockOut)
                        .toMinutes();


        double actualWorkMinutes =
                totalWorkMinutes
                - requestVO.getBreakMinutes();


        if (actualWorkMinutes < 0) {

            throw new GetOutException();
        }


        double actualWorkHours =
                actualWorkMinutes / 60.0;


        LocalDate workDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // =========================
        // 야간근로
        // =========================

        long nightMinutes =
                calculateNightMinutes(
                        workDate,
                        clockIn,
                        clockOut);


        double actualNightHours =
                nightMinutes / 60.0;


        // =========================
        // 휴일근로
        // =========================

        double actualHolidayHours =
                0;


        if ("holiday".equals(
                scheduleDto.getScheduledDayType())
                || "dayOff".equals(
                        scheduleDto.getScheduledDayType())) {

            actualHolidayHours =
                    actualWorkHours;
        }


        int employeeNo =
                contractDto.getEmployeeNo();


        warnWeeklyActualWorkLimit(
                employeeNo,
                workDate,
                scheduleDto.getWorkScheduleNo(),
                actualWorkHours
        );


        // =========================
        // Attendance
        // =========================

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
                0);


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);


        if (!attendanceResult) {

            throw new GetOutException();
        }


        // =========================
        // Schedule
        // =========================

        WorkScheduleUpdateRequestVO scheduleUpdateVO =
                WorkScheduleUpdateRequestVO.builder()
                        .workScheduleNo(
                                scheduleDto.getWorkScheduleNo())
                        .actualWorkHours(
                                actualWorkHours)
                        .actualOvertimeHours(
                                0.0)
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


        recalculateWeeklyOvertime(
                employeeNo,
                workDate);
    }



    // =====================================================
    // 비근무 -> 비근무
    // =====================================================

    @Override
    @Transactional
    public void absentToAbsent(
            AttendanceAbsentToAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {


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


        if (!"absent".equals(
                attendanceDto.getAttendanceType())
                && !"paid_leave".equals(
                        attendanceDto.getAttendanceType())
                && !"unpaid_leave".equals(
                        attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (!"absent".equals(
                requestVO.getAttendanceType())
                && !"paid_leave".equals(
                        requestVO.getAttendanceType())
                && !"unpaid_leave".equals(
                        requestVO.getAttendanceType())) {

            throw new GetOutException();
        }


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.findByContract(
                        attendanceDto.getContractNo(),
                        attendanceDto.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


        attendanceDto.setAttendanceType(
                requestVO.getAttendanceType());


        boolean attendanceResult =
                employeeAttendanceDao.update(
                        attendanceDto);


        if (!attendanceResult) {

            throw new GetOutException();
        }


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
    // 결근 등록
    // =====================================================

    @Override
    @Transactional
    public void absent(
            AttendanceAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {


        if (requestVO == null
                || requestVO.getWorkDate() == null) {

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


        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .build();


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        requestVO.getWorkDate());


        // =========================
        // 일정 없으면 관리자 판단으로
        // workday 생성
        // =========================

        if (scheduleDto == null) {


            Long contractNo =
                    contractDao.findContractByPeriod(
                            requestVO.getEmployeeNo(),
                            requestVO.getWorkDate());


            if (contractNo == null) {

                throw new TargetNotfoundException();
            }


            ContractDto contract =
                    contractDao.find(
                            contractNo);


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


            if (contract.getWeeklyHolidayDay() != null
                    && contract.getWeeklyHolidayDay()
                            .equals(absentDay)) {

                throw new GetOutException();
            }


            long workScheduleNo =
                    employeeWorkScheduleDao.sequence();


            WorkScheduleAddRequestVO scheduleAddVO =
                    new WorkScheduleAddRequestVO();


            scheduleAddVO.setWorkScheduleNo(
                    workScheduleNo);

            scheduleAddVO.setEmployeeNo(
                    requestVO.getEmployeeNo());

            scheduleAddVO.setContractNo(
                    contractNo);

            scheduleAddVO.setScheduledWorkDate(
                    requestVO.getWorkDate());

            scheduleAddVO.setScheduledClockIn(
                    null);

            scheduleAddVO.setScheduledClockOut(
                    null);

            scheduleAddVO.setScheduledDayType(
                    "workday");


            boolean scheduleResult =
                    employeeWorkScheduleDao.add(
                            scheduleAddVO);


            if (!scheduleResult) {

                throw new GetOutException();
            }


            scheduleDto =
                    employeeWorkScheduleDao.findByNo(
                            workScheduleNo);


            if (scheduleDto == null) {

                throw new TargetNotfoundException();
            }
        }


        if (!"workday".equals(
                scheduleDto.getScheduledDayType())) {

            throw new GetOutException();
        }


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
    // 유급휴가 등록
    // =====================================================

    @Override
    @Transactional
    public void paidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO) {


        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .build();


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        requestVO.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


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
    // 무급휴가 등록
    // =====================================================

    @Override
    @Transactional
    public void unpaidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO) {


        EmployeeSearchByNameVO employeeVO =
                EmployeeSearchByNameVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .build();


        EmployeeWorkScheduleDto scheduleDto =
                employeeWorkScheduleDao.find(
                        employeeVO,
                        requestVO.getWorkDate());


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


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
    // 야간근로시간 계산
    //
    // 00:00 ~ 06:00
    // 22:00 ~ 익일 06:00
    // =====================================================

    private long calculateNightMinutes(
            LocalDate workDate,
            LocalDateTime clockIn,
            LocalDateTime clockOut) {


        long nightMinutes =
                0;


        // =========================
        // 00:00 ~ 06:00
        // =========================

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


        // =========================
        // 22:00 ~ 익일 06:00
        // =========================

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


        return nightMinutes;
    }



    // =====================================================
    // 출근 전 주 52시간 검증
    //
    // KH EDU 단위기간
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


        double weeklyActualWorkHours =
                findWeeklyActualWorkHours(
                        employeeNo,
                        workDate,
                        todaySchedule.getWorkScheduleNo()
                );


        if (weeklyActualWorkHours
                >= WEEKLY_WORK_LIMIT) {

            throw new GetOutException();
        }


        ContractDto contractDto =
                contractDao.find(
                        todaySchedule.getContractNo());


        if (contractDto == null) {

            throw new TargetNotfoundException();
        }


        if (contractDto.getDailyWorkHours() == null
                || contractDto.getDailyWorkHours() <= 0) {

            throw new GetOutException();
        }


        double todayScheduledWorkHours;


        // 자동 생성 기본 workday
        if (todaySchedule.getScheduledClockIn() == null
                && todaySchedule.getScheduledClockOut() == null) {


            todayScheduledWorkHours =
                    contractDto.getDailyWorkHours();
        }


        else if (todaySchedule.getScheduledClockIn() == null
                || todaySchedule.getScheduledClockOut() == null) {

            throw new GetOutException();
        }


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


        if (weeklyActualWorkHours
                + todayScheduledWorkHours
                > WEEKLY_WORK_LIMIT) {

            throw new GetOutException();
        }
    }



    // =====================================================
    // 해당 주 실제근로시간 조회
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


        double weeklyActualWorkHours =
                0;


        for (EmployeeWorkScheduleDto scheduleDto
                : weeklyScheduleList) {


            if (excludeWorkScheduleNo != null
                    && scheduleDto.getWorkScheduleNo()
                            != null
                    && scheduleDto.getWorkScheduleNo()
                            .longValue()
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
    // 실제 주 52시간 초과 경고
    //
    // 실제 발생근로는 저장 거부하지 않는다
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



    // =====================================================
    // 주간 연장근로시간 재계산
    //
    // 1. 일 소정근로시간 초과
    // 2. 주 소정근로시간 초과
    //
    // 일 초과시간은 weekly 기본누적에서 제외하여
    // 같은 시간을 이중으로 연장처리하지 않는다.
    //
    // 휴일 / 휴무일 근로는 Calculater에서
    // holidayPay로 별도 계산하므로
    // actualOvertimeHours에서는 제외한다.
    //
    // 주중 계약 변경이 발생해도
    // 직원의 주간 기본근로 누적은 계속 이어진다.
    // 각 날짜의 주 소정 기준은
    // 해당 날짜 계약 weeklyWorkHours를 적용한다.
    // =====================================================

    private void recalculateWeeklyOvertime(
            int employeeNo,
            LocalDate targetDate) {


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


        List<EmployeeWorkScheduleDto> scheduleList =
                employeeWorkScheduleDao.findByPeriod(
                        employeeNo,
                        weekStartDate,
                        weekEndDate
                );


        scheduleList.sort(
                Comparator.comparing(
                        EmployeeWorkScheduleDto::getScheduledWorkDate
                )
        );


        // 주 전체 소정근로 누적
        // 계약이 중간에 바뀌어도 리셋하지 않음
        double weeklyBasicHours =
                0;


        for (EmployeeWorkScheduleDto scheduleDto
                : scheduleList) {


            ContractDto contractDto =
                    contractDao.find(
                            scheduleDto.getContractNo());


            if (contractDto == null) {

                throw new TargetNotfoundException();
            }


            if (contractDto.getDailyWorkHours() == null
                    || contractDto.getDailyWorkHours() <= 0) {

                throw new GetOutException();
            }


            if (contractDto.getWeeklyWorkHours() == null
                    || contractDto.getWeeklyWorkHours() <= 0) {

                throw new GetOutException();
            }


            double actualWorkHours =
                    scheduleDto.getActualWorkHours() == null
                            ? 0
                            : scheduleDto.getActualWorkHours();


            double actualOvertimeHours =
                    0;


            // =========================
            // 실제 근무가 있는 일반 workday
            // =========================

            if (actualWorkHours > 0
                    && "workday".equals(
                            scheduleDto.getScheduledDayType())) {


                double dailyWorkHours =
                        contractDto.getDailyWorkHours();


                double weeklyWorkHours =
                        contractDto.getWeeklyWorkHours();


                // 일 소정근로시간까지만
                // 주 기본근로 누적에 포함
                double dailyBasicHours =
                        Math.min(
                                actualWorkHours,
                                dailyWorkHours
                        );


                // 일 소정 초과
                double dailyOvertimeHours =
                        Math.max(
                                actualWorkHours
                                - dailyWorkHours,
                                0
                        );


                double beforeWeeklyBasicHours =
                        weeklyBasicHours;


                double afterWeeklyBasicHours =
                        beforeWeeklyBasicHours
                        + dailyBasicHours;


                // =========================
                // 이번 날짜 때문에 새로 생긴
                // 주 소정 초과분
                // =========================

                double beforeWeeklyOvertime =
                        Math.max(
                                beforeWeeklyBasicHours
                                - weeklyWorkHours,
                                0
                        );


                double afterWeeklyOvertime =
                        Math.max(
                                afterWeeklyBasicHours
                                - weeklyWorkHours,
                                0
                        );


                double weeklyOvertimeIncrement =
                        afterWeeklyOvertime
                        - beforeWeeklyOvertime;


                if (weeklyOvertimeIncrement < 0) {

                    weeklyOvertimeIncrement =
                            0;
                }


                // 일 초과 +
                // 일 초과와 중복되지 않는 주 초과
                actualOvertimeHours =
                        dailyOvertimeHours
                        + weeklyOvertimeIncrement;


                weeklyBasicHours =
                        afterWeeklyBasicHours;
            }


            // =========================
            // holiday / dayOff
            //
            // holidayPay에서 별도 처리
            // weeklyBasicHours에도 넣지 않음
            // =========================


            // =========================
            // Schedule overtime 갱신
            // =========================

            WorkScheduleUpdateRequestVO updateVO =
                    new WorkScheduleUpdateRequestVO();


            updateVO.setWorkScheduleNo(
                    scheduleDto.getWorkScheduleNo());


            updateVO.setActualOvertimeHours(
                    actualOvertimeHours);


            boolean scheduleSuccess =
                    employeeWorkScheduleDao.update(
                            updateVO);


            if (!scheduleSuccess) {

                throw new GetOutException();
            }


            // =========================
            // Attendance overtime도 동기화
            // =========================

            EmployeeAttendanceDto attendanceDto =
                    employeeAttendanceDao.findBySchedule(
                            scheduleDto.getWorkScheduleNo());


            if (attendanceDto != null) {


                attendanceDto.setOvertimeHours(
                        actualOvertimeHours);


                boolean attendanceSuccess =
                        employeeAttendanceDao.update(
                                attendanceDto);


                if (!attendanceSuccess) {

                    throw new GetOutException();
                }
            }
        }
    }
}