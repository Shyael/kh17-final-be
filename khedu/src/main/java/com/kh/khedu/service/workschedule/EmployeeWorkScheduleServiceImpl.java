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


    // 근무 일정 등록
    @Override
    public WorkScheduleAddResponseVO add(
            WorkScheduleAddRequestVO requestVO) {


        // =========================
        // 기본 입력값 확인
        // =========================

        if (requestVO.getScheduledWorkDate() == null
                || requestVO.getScheduledDayType() == null) {

            throw new GetOutException();
        }


        // =========================
        // 해당 날짜 적용 계약 조회
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
        // 근무일 계약기간 확인
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
                && (
                    contractEnd == null
                    || !date.isAfter(
                            contractEnd)
                );


        if (!contractPeriod) {

            throw new GetOutException();
        }


        // =========================
        // 예정 근무일 유형 확인
        // =========================

        if (!"workday".equals(
                requestVO.getScheduledDayType())
                && !"holiday".equals(
                        requestVO.getScheduledDayType())
                && !"dayOff".equals(
                        requestVO.getScheduledDayType())) {

            throw new GetOutException();
        }


        // =========================
        // 근무 가능한 날짜인지 확인
        // =========================

        boolean canWork =
                "workday".equals(
                        requestVO.getScheduledDayType())
                || "holiday".equals(
                        requestVO.getScheduledDayType());


        boolean hasSchedule =
                requestVO.getScheduledClockIn() != null
                && requestVO.getScheduledClockOut() != null;


        // workday / holiday는
        // 예정 출퇴근시간 필수
        if (canWork
                && !hasSchedule) {

            throw new GetOutException();
        }


        // =========================
        // 휴무일
        // =========================

        if ("dayOff".equals(
                requestVO.getScheduledDayType())) {

            requestVO.setScheduledClockIn(
                    null);

            requestVO.setScheduledClockOut(
                    null);
        }


        // =========================
        // 예정 출퇴근시간 확인
        // =========================

        if (canWork) {

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
        // 같은 날짜 일정 중복 확인
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
        // 주 52시간 예정근무 제한
        // =========================

        validateWeeklyScheduledWorkLimit(
                employeeNo,
                date,
                null,
                requestVO.getScheduledDayType(),
                requestVO.getScheduledClockIn(),
                requestVO.getScheduledClockOut(),
                existContract
        );


        // =========================
        // 일정번호 생성
        // =========================

        long workScheduleNo =
                employeeWorkScheduleDao.sequence();


        requestVO.setWorkScheduleNo(
                workScheduleNo
        );


        // =========================
        // 일정 등록
        // =========================

        boolean success =
                employeeWorkScheduleDao.add(
                        requestVO
                );


        if (!success) {

            throw new GetOutException();
        }


        // =========================
        // 결과 반환
        // =========================

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


    // 근무 일정 수정
    @Override
    @Transactional
    public void update(
            WorkScheduleUpdateRequestVO requestVO) {


        // =========================
        // 수정 대상 일정 조회
        // =========================

        EmployeeWorkScheduleDto currentSchedule =
                employeeWorkScheduleDao.findByNo(
                        requestVO.getWorkScheduleNo()
                );


        if (currentSchedule == null) {

            throw new TargetNotfoundException();
        }


        // =========================
        // 현재 일정의 계약 조회
        // =========================

        ContractDto contract =
                contractDao.find(
                        currentSchedule.getContractNo()
                );


        if (contract == null) {

            throw new TargetNotfoundException();
        }


        // =========================
        // 예정 근무일 유형 검증
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
        // 근무 날짜 변경 여부
        // =========================

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
                            requestWorkDate.atStartOfDay()
                    )
            );


            workDateChanged =
                    !requestWorkDate.equals(
                            currentWorkDate
                    );
        }


        // =========================
        // 근무일 유형 변경 여부
        // =========================

        boolean dayTypeChanged =
                requestVO.getScheduledDayType() != null
                && !requestVO.getScheduledDayType()
                        .equals(
                                currentSchedule
                                        .getScheduledDayType()
                        );


        // =========================
        // 실제 근태 존재 여부
        // =========================

        EmployeeAttendanceDto attendanceExist =
                employeeAttendanceDao.findBySchedule(
                        currentSchedule.getWorkScheduleNo()
                );


        // 실제 근태가 발생했다면
        // 날짜 / 근무일 유형 변경 불가
        if (attendanceExist != null
                && (
                    workDateChanged
                    || dayTypeChanged
                )) {

            throw new GetOutException();
        }


        // =========================
        // 날짜 변경 시 계약기간 확인
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
                    && (
                        contractEnd == null
                        || !workDate.isAfter(
                                contractEnd)
                    );


            if (!contractPeriod) {

                throw new GetOutException();
            }


            // 같은 직원의 해당 날짜 일정 중복 확인
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
        // 수정 후 최종 날짜
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
        // 수정 후 최종 근무일 유형
        // =========================

        String scheduledDayType =
                requestVO.getScheduledDayType() != null
                        ? requestVO.getScheduledDayType()
                        : currentSchedule.getScheduledDayType();


        // =========================
        // 최종 예정시간
        // =========================

        Timestamp finalScheduledClockIn =
                requestVO.getScheduledClockIn() != null
                        ? requestVO.getScheduledClockIn()
                        : currentSchedule.getScheduledClockIn();


        Timestamp finalScheduledClockOut =
                requestVO.getScheduledClockOut() != null
                        ? requestVO.getScheduledClockOut()
                        : currentSchedule.getScheduledClockOut();


        // =========================
        // 휴무일 처리
        // =========================

        if ("dayOff".equals(
                scheduledDayType)) {


            // 이번 요청으로 dayOff가 된 경우
            if ("dayOff".equals(
                    requestVO.getScheduledDayType())) {

                requestVO.setScheduledClockIn(
                        null);

                requestVO.setScheduledClockOut(
                        null);


                finalScheduledClockIn =
                        null;

                finalScheduledClockOut =
                        null;
            }


            // 기존 dayOff 상태에서
            // 근무시간만 억지로 집어넣는 요청 차단
            else if (requestVO.getScheduledClockIn() != null
                    || requestVO.getScheduledClockOut() != null) {

                throw new GetOutException();
            }
        }


        // =========================
        // workday / holiday 예정시간 검증
        // =========================

        else {

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
        // actual 값 음수 방지
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
        // 예정 일정 자체가 변경되는 경우만
        // 주 52시간 재검증
        // =========================

        boolean plannedScheduleChanged =
                workDateChanged
                || dayTypeChanged
                || requestVO.getScheduledClockIn() != null
                || requestVO.getScheduledClockOut() != null;


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


        // =========================
        // 일정 수정
        // =========================

        boolean success =
                employeeWorkScheduleDao.update(
                        requestVO
                );


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
                        scheduledWorkDate
                );


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
                        endDate
                );


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
                        LocalDateTime.now()
                );


        // 예정 퇴근시간이 지났는데
        // 근태가 없는 근무일 조회
        List<EmployeeWorkScheduleDto> scheduleList =
                employeeWorkScheduleDao
                        .findAutoAbsentTarget(
                                now
                        );


        for (EmployeeWorkScheduleDto scheduleDto
                : scheduleList) {


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


            // 결근이므로 actual 4종 0
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
    // KH EDU 기준
    // 월요일 00:00 ~ 다음 월요일 00:00 미만 = 1주
    //
    // 근무시간은 휴게시간 제외
    //
    // update의 경우 excludeWorkScheduleNo를 제외하고
    // 수정 후 예정근무시간을 다시 더함
    // =====================================================
    private void validateWeeklyScheduledWorkLimit(
            int employeeNo,
            LocalDate targetDate,
            Long excludeWorkScheduleNo,
            String targetDayType,
            Timestamp targetClockIn,
            Timestamp targetClockOut,
            ContractDto targetContract) {


        // =========================
        // 해당 날짜가 속한 주 시작
        // =========================

        LocalDate weekStart =
                targetDate.with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY
                        )
                );


        // 다음주 월요일
        // end exclusive
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


        // =========================
        // 해당 주 기존 스케줄 조회
        // =========================

        List<EmployeeWorkScheduleDto> weeklyScheduleList =
                employeeWorkScheduleDao.findByPeriod(
                        employeeNo,
                        weekStartDate,
                        weekEndDate
                );


        double weeklyScheduledWorkHours = 0;


        // =========================
        // 기존 예정근무시간 합계
        // =========================

        for (EmployeeWorkScheduleDto scheduleDto
                : weeklyScheduleList) {


            // update 중인 현재 일정은 제외
            if (excludeWorkScheduleNo != null
                    && scheduleDto.getWorkScheduleNo()
                            == excludeWorkScheduleNo) {

                continue;
            }


            // 휴무일은 근무시간 없음
            if ("dayOff".equals(
                    scheduleDto.getScheduledDayType())) {

                continue;
            }


            if (scheduleDto.getScheduledClockIn() == null
                    || scheduleDto.getScheduledClockOut() == null) {

                throw new GetOutException();
            }


            // 계약이 주중 변경될 수도 있으므로
            // 각 스케줄 자신의 contractNo 기준으로 조회
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


            double scheduledMinutes =
                    Duration.between(
                            scheduledClockIn,
                            scheduledClockOut
                    ).toMinutes()
                    - scheduleContract
                            .getWrittenBreakMinutes();


            if (scheduledMinutes < 0) {

                throw new GetOutException();
            }


            weeklyScheduledWorkHours +=
                    scheduledMinutes / 60.0;
        }


        // =========================
        // 추가 / 수정될 일정의 근무시간
        // =========================

        double targetScheduledWorkHours = 0;


        if (!"dayOff".equals(
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


            double scheduledMinutes =
                    Duration.between(
                            scheduledClockIn,
                            scheduledClockOut
                    ).toMinutes()
                    - targetContract
                            .getWrittenBreakMinutes();


            if (scheduledMinutes < 0) {

                throw new GetOutException();
            }


            targetScheduledWorkHours =
                    scheduledMinutes / 60.0;
        }


        // =========================
        // 최종 주간 예정근로시간
        // =========================

        double expectedWeeklyWorkHours =
                weeklyScheduledWorkHours
                + targetScheduledWorkHours;


        if (expectedWeeklyWorkHours
                > WEEKLY_WORK_LIMIT) {

            throw new GetOutException();
        }
    }
}