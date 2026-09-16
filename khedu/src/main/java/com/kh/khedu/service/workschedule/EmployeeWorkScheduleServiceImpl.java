package com.kh.khedu.service.workschedule;

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

    private static final double WEEKLY_WORK_LIMIT = 52.0;

    @Autowired
    private EmployeeWorkScheduleDao employeeWorkScheduleDao;

    @Autowired
    private EmployeeAttendanceDao employeeAttendanceDao;

    @Autowired
    private ContractDao contractDao;


    // =====================================================
    // 근무 일정 등록
    // =====================================================

    @Override
    public WorkScheduleAddResponseVO add(
            WorkScheduleAddRequestVO requestVO) {


        // =========================
        // 기본 입력값
        // =========================

        if (requestVO.getScheduledWorkDate() == null
                || requestVO.getScheduledDayType() == null) {

            throw new GetOutException();
        }


        // =========================
        // 해당 날짜 적용 계약
        // =========================

        Long contractNo =
                contractDao.findContractByPeriod(
                        requestVO.getEmployeeNo(),
                        requestVO.getScheduledWorkDate()
                );


        if (contractNo == null) {

            throw new TargetNotfoundException();
        }


        ContractDto existContract =
                contractDao.find(
                        contractNo
                );


        if (existContract == null) {

            throw new TargetNotfoundException();
        }


        requestVO.setContractNo(
                existContract.getContractNo()
        );


        // =========================
        // 계약기간 확인
        // =========================

        LocalDate date =
                requestVO.getScheduledWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        LocalDate contractStart =
                existContract.getContractStart()
                        .toLocalDateTime()
                        .toLocalDate();


        LocalDate contractEnd =
                existContract.getContractEnd() == null
                        ? null
                        : existContract.getContractEnd()
                                .toLocalDateTime()
                                .toLocalDate();


        boolean contractPeriod =
                !date.isBefore(
                        contractStart)
                &&
                (
                    contractEnd == null
                    ||
                    !date.isAfter(
                            contractEnd)
                );


        if (!contractPeriod) {

            throw new GetOutException();
        }


        // =========================
        // 근무일 유형 확인
        // =========================

        String scheduledDayType =
                requestVO.getScheduledDayType();


        if (!"workday".equals(
                scheduledDayType)
                && !"holiday".equals(
                        scheduledDayType)
                && !"dayOff".equals(
                        scheduledDayType)) {

            throw new GetOutException();
        }


        // =========================
        // 일반 근무일
        //
        // 원장이 예정 출퇴근시간 입력
        // =========================

        if ("workday".equals(
                scheduledDayType)) {


            if (requestVO.getScheduledClockIn() == null
                    || requestVO.getScheduledClockOut() == null) {

                throw new GetOutException();
            }


            LocalDateTime scheduledClockIn =
                    requestVO.getScheduledClockIn()
                            .toLocalDateTime();


            LocalDateTime scheduledClockOut =
                    requestVO.getScheduledClockOut()
                            .toLocalDateTime();


            if (!scheduledClockOut.isAfter(
                    scheduledClockIn)) {

                throw new GetOutException();
            }
        }


        // =========================
        // 주휴일 / 휴무일
        //
        // 예정 출퇴근시간 없음
        //
        // 주휴일 실제 출퇴근은
        // 직원의 출근 / 퇴근으로 기록
        // =========================

        else {

            requestVO.setScheduledClockIn(
                    null
            );

            requestVO.setScheduledClockOut(
                    null
            );
        }


        // =========================
        // 같은 날짜 일정 중복
        // =========================

        int employeeNo =
                existContract.getEmployeeNo();


        EmployeeWorkScheduleDto schedule =
                employeeWorkScheduleDao.isExist(
                        employeeNo,
                        requestVO.getScheduledWorkDate()
                );


        if (schedule != null) {

            throw new GetOutException();
        }


        // =========================
        // 주 52시간 예정근무 검증
        //
        // workday만 예정근무에 포함
        // holiday/dayOff는 예정시간 없음
        // =========================

        validateWeeklyScheduledWorkLimit(
                employeeNo,
                date,
                null,
                scheduledDayType,
                requestVO.getScheduledClockIn(),
                requestVO.getScheduledClockOut(),
                existContract
        );


        // =========================
        // 일정번호
        // =========================

        long workScheduleNo =
                employeeWorkScheduleDao.sequence();


        requestVO.setWorkScheduleNo(
                workScheduleNo
        );


        // =========================
        // 등록
        // =========================

        boolean success =
                employeeWorkScheduleDao.add(
                        requestVO
                );


        if (!success) {

            throw new GetOutException();
        }


        return WorkScheduleAddResponseVO.builder()
                .scheduledClockIn(
                        requestVO.getScheduledClockIn())
                .scheduledClockOut(
                        requestVO.getScheduledClockOut())
                .scheduledDayType(
                        requestVO.getScheduledDayType())
                .scheduledWorkDate(
                        requestVO.getScheduledWorkDate())
                .contractNo(
                        requestVO.getContractNo())
                .build();
    }


    // =====================================================
    // 근무 일정 수정
    // =====================================================

    @Override
    @Transactional
    public void update(
            WorkScheduleUpdateRequestVO requestVO) {


        // =========================
        // 수정 대상
        // =========================

        EmployeeWorkScheduleDto currentSchedule =
                employeeWorkScheduleDao.findByNo(
                        requestVO.getWorkScheduleNo()
                );


        if (currentSchedule == null) {

            throw new TargetNotfoundException();
        }


        ContractDto contract =
                contractDao.find(
                        currentSchedule.getContractNo()
                );


        if (contract == null) {

            throw new TargetNotfoundException();
        }


        // =========================
        // 근무일 유형 검증
        // =========================

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


        // =========================
        // 날짜 변경
        // =========================

        boolean workDateChanged =
                false;


        if (requestVO.getScheduledWorkDate() != null) {


            LocalDate requestWorkDate =
                    requestVO.getScheduledWorkDate()
                            .toLocalDateTime()
                            .toLocalDate();


            LocalDate currentWorkDate =
                    currentSchedule
                            .getScheduledWorkDate()
                            .toLocalDateTime()
                            .toLocalDate();


            requestVO.setScheduledWorkDate(
                    Timestamp.valueOf(
                            requestWorkDate.atStartOfDay()
                    )
            );


            workDateChanged =
                    !requestWorkDate.equals(
                            currentWorkDate
                    );
        }


        // =========================
        // 유형 변경 여부
        // =========================

        boolean dayTypeChanged =
                requestVO.getScheduledDayType() != null
                &&
                !requestVO.getScheduledDayType()
                        .equals(
                                currentSchedule
                                        .getScheduledDayType()
                        );


        // =========================
        // 실제 근태 존재
        // =========================

        EmployeeAttendanceDto attendanceExist =
                employeeAttendanceDao.findBySchedule(
                        currentSchedule.getWorkScheduleNo()
                );


        // 실제 근태 발생 후
        // 날짜 / 근무일 유형 변경 금지
        if (attendanceExist != null
                &&
                (
                    workDateChanged
                    ||
                    dayTypeChanged
                )) {

            throw new GetOutException();
        }


        // =========================
        // 날짜 변경 시 계약기간
        // =========================

        if (workDateChanged) {


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


            boolean contractPeriod =
                    !workDate.isBefore(
                            contractStart)
                    &&
                    (
                        contractEnd == null
                        ||
                        !workDate.isAfter(
                                contractEnd)
                    );


            if (!contractPeriod) {

                throw new GetOutException();
            }


            EmployeeWorkScheduleDto alreadyExist =
                    employeeWorkScheduleDao.isExist(
                            contract.getEmployeeNo(),
                            requestVO.getScheduledWorkDate()
                    );


            if (alreadyExist != null) {

                throw new GetOutException();
            }
        }


        // =========================
        // 수정 후 날짜
        // =========================

        LocalDate finalWorkDate =
                requestVO.getScheduledWorkDate() != null

                        ? requestVO
                                .getScheduledWorkDate()
                                .toLocalDateTime()
                                .toLocalDate()

                        : currentSchedule
                                .getScheduledWorkDate()
                                .toLocalDateTime()
                                .toLocalDate();


        // =========================
        // 수정 후 유형
        // =========================

        String scheduledDayType =
                requestVO.getScheduledDayType() != null

                        ? requestVO.getScheduledDayType()

                        : currentSchedule
                                .getScheduledDayType();


        // =========================
        // 수정 후 예정시간
        // =========================

        Timestamp finalScheduledClockIn =
                requestVO.getScheduledClockIn() != null

                        ? requestVO.getScheduledClockIn()

                        : currentSchedule
                                .getScheduledClockIn();


        Timestamp finalScheduledClockOut =
                requestVO.getScheduledClockOut() != null

                        ? requestVO.getScheduledClockOut()

                        : currentSchedule
                                .getScheduledClockOut();


        // =========================
        // workday
        // =========================

        if ("workday".equals(
                scheduledDayType)) {


            if (finalScheduledClockIn == null
                    || finalScheduledClockOut == null) {

                throw new GetOutException();
            }


            LocalDateTime clockIn =
                    finalScheduledClockIn
                            .toLocalDateTime();


            LocalDateTime clockOut =
                    finalScheduledClockOut
                            .toLocalDateTime();


            if (!clockOut.isAfter(
                    clockIn)) {

                throw new GetOutException();
            }
        }


        // =========================
        // holiday / dayOff
        //
        // 원장이 예정시간을 가지지 않음
        // =========================

        else {


            requestVO.setScheduledClockIn(
                    null
            );

            requestVO.setScheduledClockOut(
                    null
            );


            finalScheduledClockIn =
                    null;

            finalScheduledClockOut =
                    null;
        }


        // =========================
        // actual 음수 방지
        // =========================

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


        // =========================
        // 예정 일정 변경 시
        // 52시간 재검증
        // =========================

        boolean plannedScheduleChanged =
                workDateChanged
                ||
                dayTypeChanged
                ||
                requestVO.getScheduledClockIn() != null
                ||
                requestVO.getScheduledClockOut() != null;


        if (plannedScheduleChanged) {


            validateWeeklyScheduledWorkLimit(
                    contract.getEmployeeNo(),
                    finalWorkDate,
                    currentSchedule.getWorkScheduleNo(),
                    scheduledDayType,
                    finalScheduledClockIn,
                    finalScheduledClockOut,
                    contract
            );
        }


        boolean success =
                employeeWorkScheduleDao.update(
                        requestVO
                );


        if (!success) {

            throw new TargetNotfoundException();
        }
    }


    // =====================================================
    // 특정 날짜 일정 조회
    // =====================================================

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
                        scheduledWorkDate
                );


        if (scheduleDto == null) {

            throw new TargetNotfoundException();
        }


        return WorkScheduleResponseVO.builder()
                .workScheduleNo(
                        scheduleDto.getWorkScheduleNo())
                .contractNo(
                        scheduleDto.getContractNo())
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


    // =====================================================
    // 기간 조회
    // =====================================================

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
                        endDate
                );


        double totalWorkHours =
                0;

        double totalOvertimeHours =
                0;

        double totalNightHours =
                0;

        double totalHolidayHours =
                0;


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


    // =====================================================
    // 자동 결근
    // =====================================================

    @Override
    @Transactional
    public void autoAbsent() {


        Timestamp now =
                Timestamp.valueOf(
                        LocalDateTime.now()
                );


        List<EmployeeWorkScheduleDto> scheduleList =
                employeeWorkScheduleDao
                        .findAutoAbsentTarget(
                                now
                        );


        for (EmployeeWorkScheduleDto scheduleDto
                : scheduleList) {


            // 방어
            // 주휴일 / 휴무일은 자동결근 대상 아님
            if (!"workday".equals(
                    scheduleDto.getScheduledDayType())) {

                continue;
            }


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
                            attendanceDto
                    );


            if (!attendanceResult) {

                throw new GetOutException();
            }


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
                            scheduleUpdateVO
                    );


            if (!scheduleResult) {

                throw new GetOutException();
            }
        }
    }


    // =====================================================
    // 주 52시간 예정근무 검증
    //
    // 월요일 00:00
    // ~ 다음 월요일 00:00 미만
    //
    // 예정근로시간은 workday만 계산
    //
    // holiday:
    // 원장이 시간을 예정하지 않고
    // 직원 실제 출퇴근으로만 기록
    //
    // dayOff:
    // 근무 예정 없음
    // =====================================================

    private void validateWeeklyScheduledWorkLimit(
            int employeeNo,
            LocalDate targetDate,
            Long excludeWorkScheduleNo,
            String targetDayType,
            Timestamp targetClockIn,
            Timestamp targetClockOut,
            ContractDto targetContract) {


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


        double weeklyScheduledWorkHours =
                0;


        // =========================
        // 기존 일정
        // =========================

        for (EmployeeWorkScheduleDto scheduleDto
                : weeklyScheduleList) {


            // update 대상 자기 자신 제외
            if (excludeWorkScheduleNo != null
                    && excludeWorkScheduleNo.equals(
                            scheduleDto.getWorkScheduleNo()
                    )) {

                continue;
            }


            // 예정시간 계산은 workday만
            if (!"workday".equals(
                    scheduleDto.getScheduledDayType())) {

                continue;
            }


            if (scheduleDto.getScheduledClockIn() == null
                    || scheduleDto.getScheduledClockOut() == null) {

                throw new GetOutException();
            }


            ContractDto scheduleContract =
                    contractDao.find(
                            scheduleDto.getContractNo()
                    );


            if (scheduleContract == null) {

                throw new TargetNotfoundException();
            }


            LocalDateTime scheduledClockIn =
                    scheduleDto.getScheduledClockIn()
                            .toLocalDateTime();


            LocalDateTime scheduledClockOut =
                    scheduleDto.getScheduledClockOut()
                            .toLocalDateTime();


            if (!scheduledClockOut.isAfter(
                    scheduledClockIn)) {

                throw new GetOutException();
            }


            double breakMinutes =
                    scheduleContract.getWrittenBreakMinutes() == null
                            ? 0
                            : scheduleContract.getWrittenBreakMinutes();


            double scheduledMinutes =
                    Duration.between(
                            scheduledClockIn,
                            scheduledClockOut
                    ).toMinutes()
                    - breakMinutes;


            if (scheduledMinutes < 0) {

                throw new GetOutException();
            }


            weeklyScheduledWorkHours +=
                    scheduledMinutes / 60.0;
        }


        // =========================
        // 새로 추가 / 수정할 일정
        // =========================

        double targetScheduledWorkHours =
                0;


        if ("workday".equals(
                targetDayType)) {


            if (targetClockIn == null
                    || targetClockOut == null) {

                throw new GetOutException();
            }


            LocalDateTime scheduledClockIn =
                    targetClockIn
                            .toLocalDateTime();


            LocalDateTime scheduledClockOut =
                    targetClockOut
                            .toLocalDateTime();


            if (!scheduledClockOut.isAfter(
                    scheduledClockIn)) {

                throw new GetOutException();
            }


            double breakMinutes =
                    targetContract.getWrittenBreakMinutes() == null
                            ? 0
                            : targetContract.getWrittenBreakMinutes();


            double scheduledMinutes =
                    Duration.between(
                            scheduledClockIn,
                            scheduledClockOut
                    ).toMinutes()
                    - breakMinutes;


            if (scheduledMinutes < 0) {

                throw new GetOutException();
            }


            targetScheduledWorkHours =
                    scheduledMinutes / 60.0;
        }


        double expectedWeeklyWorkHours =
                weeklyScheduledWorkHours
                + targetScheduledWorkHours;


        if (expectedWeeklyWorkHours
                > WEEKLY_WORK_LIMIT) {

            throw new GetOutException();
        }
    }
}