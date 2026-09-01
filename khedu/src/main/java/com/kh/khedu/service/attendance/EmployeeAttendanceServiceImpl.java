package com.kh.khedu.service.attendance;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToNormalRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceClockInRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceLeaveRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToNormalRequestVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockInResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockOutResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceFindVO;
import com.kh.khedu.vo.payroll.response.AttendanceSearchResponseVO;

@Service
@Transactional
public class EmployeeAttendanceServiceImpl
        implements EmployeeAttendanceService {

    @Autowired
    private EmployeeAttendanceDao employeeAttendanceDao;

    @Autowired
    private ContractDao contractDao;
    

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


        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                employeeVO.getEmployeeNo())
                        .working(true)
                        .build();


        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);


        return attendanceDto != null;
    }
    
    
    
    
    @Override
    public AttendanceClockInResponseVO clockIn(
            AttendanceClockInRequestVO requestVO,
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

        int employeeNo =
                employeeVO.getEmployeeNo();


        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime workDate =
                now.toLocalDate()
                        .atStartOfDay();


        // 아직 퇴근하지 않은 근태가 있는지 확인
        AttendanceFindVO workingFindVO =
                AttendanceFindVO.builder()
                        .employeeNo(employeeNo)
                        .working(true)
                        .build();

        EmployeeAttendanceDto workingDto =
                employeeAttendanceDao.find(
                        workingFindVO);

        if (workingDto != null) {
            throw new GetOutException();
        }


        // 현재 근로계약 조회
        ContractDto contractDto =
                contractDao.findCurrent(
                        employeeNo);

        if (contractDto == null) {
            throw new TargetNotfoundException();
        }


        // 오늘 이미 근태가 생성됐는지 확인
        AttendanceFindVO todayFindVO =
                AttendanceFindVO.builder()
                        .employeeNo(employeeNo)
                        .workDate(
                                Timestamp.valueOf(
                                        workDate))
                        .build();

        EmployeeAttendanceDto todayDto =
                employeeAttendanceDao.find(
                        todayFindVO);

        if (todayDto != null) {
            throw new GetOutException();
        }


        // 출근 근태 생성
        EmployeeAttendanceDto attendanceDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                contractDto.getContractNo())
                        .workDate(
                                Timestamp.valueOf(
                                        workDate))
                        .clockIn(
                                Timestamp.valueOf(
                                        now))
                        .clockOut(null)
                        .breakMinutes(
                                (double) contractDto.getWrittenBreakMinutes())
                        .attendanceType(
                                "정상")
                        .workDayType(
                                requestVO.getWorkDayType())
                        .nightHours(0)
                        .overtimeHours(0)
                        .build();


        boolean result =
                employeeAttendanceDao.add(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }


        // 출근 결과 반환
        return AttendanceClockInResponseVO.builder()
                .empAttendanceNo(
                        attendanceDto.getEmpAttendanceNo())
                .employeeNo(
                        employeeVO.getEmployeeNo())
                .accountName(
                        employeeVO.getAccountName())
                .workDate(
                        attendanceDto.getWorkDate())
                .clockIn(
                        attendanceDto.getClockIn())
                .breakMinutes(
                        attendanceDto.getBreakMinutes())
                .workDayType(
                        attendanceDto.getWorkDayType())
                .build();
    }

    // 퇴근
    @Override
    public AttendanceClockOutResponseVO clockOut(
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


        int employeeNo =
                employeeVO.getEmployeeNo();


        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(employeeNo)
                        .working(true)
                        .build();

        EmployeeAttendanceDto attendanceDto =
                employeeAttendanceDao.find(
                        findVO);


        if (attendanceDto == null) {
            throw new TargetNotfoundException();
        }

        if (attendanceDto.getClockIn() == null) {
            throw new GetOutException();
        }


        // Timestamp -> LocalDateTime
        LocalDateTime clockIn =
                attendanceDto.getClockIn()
                        .toLocalDateTime();

        LocalDateTime clockOut =
                LocalDateTime.now();


        if (!clockOut.isAfter(clockIn)) {
            throw new GetOutException();
        }


        if (attendanceDto.getBreakMinutes() == null
                || attendanceDto.getBreakMinutes() < 0) {

            throw new GetOutException();
        }


        // 계약상 근로시간 조회
        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());

        if (contractDto == null) {
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
                        - attendanceDto.getBreakMinutes();

        if (actualWorkMinutes < 0) {
            throw new GetOutException();
        }


        // 계약상 하루 근무시간
        double contractWorkMinutes =
                contractDto.getDailyWorkHours()
                        * 60;


        // 연장근무 계산
        double overtimeMinutes =
                actualWorkMinutes
                        - contractWorkMinutes;

        if (overtimeMinutes < 0) {
            overtimeMinutes = 0;
        }


        // 야간근무 계산
        // 근태일 00:00 ~ 06:00
        // 근태일 22:00 ~ 익일 06:00
        long nightWorkMinutes = 0;

        LocalDate attendanceDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // 근태일 00:00 ~ 06:00
        LocalDateTime earlyNightStart =
                attendanceDate.atStartOfDay();

        LocalDateTime earlyNightEnd =
                attendanceDate.atTime(
                        6,
                        0);


        LocalDateTime actualEarlyNightStart;

        if (clockIn.isAfter(
                earlyNightStart)) {

            actualEarlyNightStart = clockIn;
        }
        else {
            actualEarlyNightStart =
                    earlyNightStart;
        }


        LocalDateTime actualEarlyNightEnd;

        if (clockOut.isBefore(
                earlyNightEnd)) {

            actualEarlyNightEnd = clockOut;
        }
        else {
            actualEarlyNightEnd =
                    earlyNightEnd;
        }


        if (actualEarlyNightEnd.isAfter(
                actualEarlyNightStart)) {

            nightWorkMinutes +=
                    Duration.between(
                            actualEarlyNightStart,
                            actualEarlyNightEnd)
                            .toMinutes();
        }


        // 근태일 22:00 ~ 익일 06:00
        LocalDateTime lateNightStart =
                attendanceDate.atTime(
                        22,
                        0);

        LocalDateTime lateNightEnd =
                attendanceDate.plusDays(1)
                        .atTime(
                                6,
                                0);


        LocalDateTime actualLateNightStart;

        if (clockIn.isAfter(
                lateNightStart)) {

            actualLateNightStart = clockIn;
        }
        else {
            actualLateNightStart =
                    lateNightStart;
        }


        LocalDateTime actualLateNightEnd;

        if (clockOut.isBefore(
                lateNightEnd)) {

            actualLateNightEnd = clockOut;
        }
        else {
            actualLateNightEnd =
                    lateNightEnd;
        }


        if (actualLateNightEnd.isAfter(
                actualLateNightStart)) {

            nightWorkMinutes +=
                    Duration.between(
                            actualLateNightStart,
                            actualLateNightEnd)
                            .toMinutes();
        }


        // 연장근무와 야간근무는 독립 계산
        // 같은 시간이 연장이면서 야간이면 양쪽 모두 포함
        attendanceDto.setClockOut(
                Timestamp.valueOf(
                        clockOut));

        attendanceDto.setNightHours(
                nightWorkMinutes / 60.0);

        attendanceDto.setOvertimeHours(
                overtimeMinutes / 60.0);


        boolean result =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }


        return AttendanceClockOutResponseVO.builder()
                .empAttendanceNo(
                        attendanceDto.getEmpAttendanceNo())
                .clockIn(
                        attendanceDto.getClockIn())
                .clockOut(
                        attendanceDto.getClockOut())
                .breakMinutes(
                        attendanceDto.getBreakMinutes())
                .workDayType(
                        attendanceDto.getWorkDayType())
                .nightHours(
                        attendanceDto.getNightHours())
                .overtimeHours(
                        attendanceDto.getOvertimeHours())
                .build();
    }


    // 결근
    @Override
    public void absent(
            AttendanceAbsentRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        if (requestVO.getWorkDate() == null) {
            throw new GetOutException();
        }


        LocalDateTime workDate =
                requestVO.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay();


        // 미래 결근 등록 불가
        if (workDate.toLocalDate()
                .isAfter(LocalDate.now())) {

            throw new GetOutException();
        }


        // 직원의 전체 근로계약 조회
        List<ContractDto> contractList =
                contractDao.findAllByEmployee(
                        requestVO.getEmployeeNo());

        if (contractList == null
                || contractList.isEmpty()) {

            throw new TargetNotfoundException();
        }


        // 해당 날짜에 적용되는 계약 판단
        ContractDto contractDto = null;

        for (ContractDto dto : contractList) {

            if (dto.getContractStart() == null) {
                continue;
            }


            LocalDateTime contractStart =
                    dto.getContractStart()
                            .toLocalDateTime();

            LocalDateTime contractEnd = null;


            if (dto.getContractEnd() != null) {

                contractEnd =
                        dto.getContractEnd()
                                .toLocalDateTime();
            }


            if (!workDate.isBefore(contractStart)
                    && (contractEnd == null
                    || workDate.isBefore(contractEnd))) {

                contractDto = dto;
                break;
            }
        }


        if (contractDto == null) {
            throw new TargetNotfoundException();
        }


        // 같은 날짜 기존 근태 확인
        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .workDate(
                                Timestamp.valueOf(
                                        workDate))
                        .build();

        EmployeeAttendanceDto existsDto =
                employeeAttendanceDao.find(
                        findVO);

        if (existsDto != null) {
            throw new GetOutException();
        }


        EmployeeAttendanceDto attendanceDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                contractDto.getContractNo())
                        .workDate(
                                Timestamp.valueOf(workDate))
                        .clockIn(null)
                        .clockOut(null)
                        .breakMinutes(0.0)
                        .attendanceType("결근")
                        .workDayType("근무일")
                        .nightHours(0)
                        .overtimeHours(0)
                        .build();


        boolean result =
                employeeAttendanceDao.add(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 유급휴가
    @Override
    public void paidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        if (requestVO.getWorkDate() == null) {
            throw new GetOutException();
        }


        LocalDateTime workDate =
                requestVO.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay();


        if (workDate.toLocalDate()
                .isAfter(LocalDate.now())) {

            throw new GetOutException();
        }


        List<ContractDto> contractList =
                contractDao.findAllByEmployee(
                        requestVO.getEmployeeNo());

        if (contractList == null
                || contractList.isEmpty()) {

            throw new TargetNotfoundException();
        }


        ContractDto contractDto = null;

        for (ContractDto dto : contractList) {

            if (dto.getContractStart() == null) {
                continue;
            }


            LocalDateTime contractStart =
                    dto.getContractStart()
                            .toLocalDateTime();

            LocalDateTime contractEnd = null;


            if (dto.getContractEnd() != null) {

                contractEnd =
                        dto.getContractEnd()
                                .toLocalDateTime();
            }


            if (!workDate.isBefore(contractStart)
                    && (contractEnd == null
                    || workDate.isBefore(contractEnd))) {

                contractDto = dto;
                break;
            }
        }


        if (contractDto == null) {
            throw new TargetNotfoundException();
        }


        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .workDate(
                                Timestamp.valueOf(
                                        workDate))
                        .build();

        EmployeeAttendanceDto existsDto =
                employeeAttendanceDao.find(
                        findVO);

        if (existsDto != null) {
            throw new GetOutException();
        }


        EmployeeAttendanceDto attendanceDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                contractDto.getContractNo())
                        .workDate(
                                Timestamp.valueOf(workDate))
                        .clockIn(null)
                        .clockOut(null)
                        .breakMinutes(0.0)
                        .attendanceType("유급휴가")
                        .workDayType("근무일")
                        .nightHours(0)
                        .overtimeHours(0)
                        .build();


        boolean result =
                employeeAttendanceDao.add(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 무급휴가
    @Override
    public void unpaidLeave(
            AttendanceLeaveRequestVO requestVO,
            TokenParseResponseVO parseVO) {

        if (requestVO.getWorkDate() == null) {
            throw new GetOutException();
        }


        LocalDateTime workDate =
                requestVO.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay();


        if (workDate.toLocalDate()
                .isAfter(LocalDate.now())) {

            throw new GetOutException();
        }


        List<ContractDto> contractList =
                contractDao.findAllByEmployee(
                        requestVO.getEmployeeNo());

        if (contractList == null
                || contractList.isEmpty()) {

            throw new TargetNotfoundException();
        }


        ContractDto contractDto = null;

        for (ContractDto dto : contractList) {

            if (dto.getContractStart() == null) {
                continue;
            }


            LocalDateTime contractStart =
                    dto.getContractStart()
                            .toLocalDateTime();

            LocalDateTime contractEnd = null;


            if (dto.getContractEnd() != null) {

                contractEnd =
                        dto.getContractEnd()
                                .toLocalDateTime();
            }


            if (!workDate.isBefore(contractStart)
                    && (contractEnd == null
                    || workDate.isBefore(contractEnd))) {

                contractDto = dto;
                break;
            }
        }


        if (contractDto == null) {
            throw new TargetNotfoundException();
        }


        AttendanceFindVO findVO =
                AttendanceFindVO.builder()
                        .employeeNo(
                                requestVO.getEmployeeNo())
                        .workDate(
                                Timestamp.valueOf(
                                        workDate))
                        .build();

        EmployeeAttendanceDto existsDto =
                employeeAttendanceDao.find(
                        findVO);

        if (existsDto != null) {
            throw new GetOutException();
        }


        EmployeeAttendanceDto attendanceDto =
                EmployeeAttendanceDto.builder()
                        .empAttendanceNo(
                                employeeAttendanceDao.sequence())
                        .contractNo(
                                contractDto.getContractNo())
                        .workDate(
                                Timestamp.valueOf(workDate))
                        .clockIn(null)
                        .clockOut(null)
                        .breakMinutes(0.0)
                        .attendanceType("무급휴가")
                        .workDayType("근무일")
                        .nightHours(0)
                        .overtimeHours(0)
                        .build();


        boolean result =
                employeeAttendanceDao.add(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 정상 -> 정상
    @Override
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


        if (!"정상".equals(
                attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (requestVO.getClockIn() == null
                || requestVO.getClockOut() == null
                || requestVO.getBreakMinutes() == null) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                requestVO.getClockIn()
                        .toLocalDateTime();

        LocalDateTime clockOut =
                requestVO.getClockOut()
                        .toLocalDateTime();


        if (!clockOut.isAfter(clockIn)) {
            throw new GetOutException();
        }


        if (requestVO.getBreakMinutes() < 0) {
            throw new GetOutException();
        }


        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());

        if (contractDto == null) {
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


        double contractWorkMinutes =
                contractDto.getDailyWorkHours()
                        * 60;


        // 연장근무
        double overtimeMinutes =
                actualWorkMinutes
                        - contractWorkMinutes;

        if (overtimeMinutes < 0) {
            overtimeMinutes = 0;
        }


        // 야간근무
        long nightWorkMinutes = 0;

        LocalDate attendanceDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // 근태일 00:00 ~ 06:00
        LocalDateTime earlyNightStart =
                attendanceDate.atStartOfDay();

        LocalDateTime earlyNightEnd =
                attendanceDate.atTime(
                        6,
                        0);


        LocalDateTime actualEarlyNightStart;

        if (clockIn.isAfter(
                earlyNightStart)) {

            actualEarlyNightStart = clockIn;
        }
        else {
            actualEarlyNightStart =
                    earlyNightStart;
        }


        LocalDateTime actualEarlyNightEnd;

        if (clockOut.isBefore(
                earlyNightEnd)) {

            actualEarlyNightEnd = clockOut;
        }
        else {
            actualEarlyNightEnd =
                    earlyNightEnd;
        }


        if (actualEarlyNightEnd.isAfter(
                actualEarlyNightStart)) {

            nightWorkMinutes +=
                    Duration.between(
                            actualEarlyNightStart,
                            actualEarlyNightEnd)
                            .toMinutes();
        }


        // 근태일 22:00 ~ 익일 06:00
        LocalDateTime lateNightStart =
                attendanceDate.atTime(
                        22,
                        0);

        LocalDateTime lateNightEnd =
                attendanceDate.plusDays(1)
                        .atTime(
                                6,
                                0);


        LocalDateTime actualLateNightStart;

        if (clockIn.isAfter(
                lateNightStart)) {

            actualLateNightStart = clockIn;
        }
        else {
            actualLateNightStart =
                    lateNightStart;
        }


        LocalDateTime actualLateNightEnd;

        if (clockOut.isBefore(
                lateNightEnd)) {

            actualLateNightEnd = clockOut;
        }
        else {
            actualLateNightEnd =
                    lateNightEnd;
        }


        if (actualLateNightEnd.isAfter(
                actualLateNightStart)) {

            nightWorkMinutes +=
                    Duration.between(
                            actualLateNightStart,
                            actualLateNightEnd)
                            .toMinutes();
        }


        attendanceDto.setClockIn(
                Timestamp.valueOf(clockIn));

        attendanceDto.setClockOut(
                Timestamp.valueOf(clockOut));

        attendanceDto.setBreakMinutes(
                requestVO.getBreakMinutes());

        attendanceDto.setWorkDayType(
                requestVO.getWorkDayType());

        attendanceDto.setNightHours(
                nightWorkMinutes / 60.0);

        attendanceDto.setOvertimeHours(
                overtimeMinutes / 60.0);


        boolean result =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 정상 -> 비근무
    @Override
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


        if (!"정상".equals(
                attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (!"결근".equals(
                requestVO.getAttendanceType())
                && !"유급휴가".equals(
                        requestVO.getAttendanceType())
                && !"무급휴가".equals(
                        requestVO.getAttendanceType())) {

            throw new GetOutException();
        }


        attendanceDto.setAttendanceType(
                requestVO.getAttendanceType());


        boolean result =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 비근무 -> 정상
    @Override
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


        if (!"결근".equals(
                attendanceDto.getAttendanceType())
                && !"유급휴가".equals(
                        attendanceDto.getAttendanceType())
                && !"무급휴가".equals(
                        attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        if (requestVO.getClockIn() == null
                || requestVO.getClockOut() == null
                || requestVO.getBreakMinutes() == null) {

            throw new GetOutException();
        }


        LocalDateTime clockIn =
                requestVO.getClockIn()
                        .toLocalDateTime();

        LocalDateTime clockOut =
                requestVO.getClockOut()
                        .toLocalDateTime();


        if (!clockOut.isAfter(clockIn)) {
            throw new GetOutException();
        }


        if (requestVO.getBreakMinutes() < 0) {
            throw new GetOutException();
        }


        ContractDto contractDto =
                contractDao.findWorkTimeCondition(
                        attendanceDto.getContractNo());

        if (contractDto == null) {
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


        double contractWorkMinutes =
                contractDto.getDailyWorkHours()
                        * 60;


        // 연장근무
        double overtimeMinutes =
                actualWorkMinutes
                        - contractWorkMinutes;

        if (overtimeMinutes < 0) {
            overtimeMinutes = 0;
        }


        // 야간근무
        long nightWorkMinutes = 0;

        LocalDate attendanceDate =
                attendanceDto.getWorkDate()
                        .toLocalDateTime()
                        .toLocalDate();


        // 근태일 00:00 ~ 06:00
        LocalDateTime earlyNightStart =
                attendanceDate.atStartOfDay();

        LocalDateTime earlyNightEnd =
                attendanceDate.atTime(
                        6,
                        0);


        LocalDateTime actualEarlyNightStart;

        if (clockIn.isAfter(
                earlyNightStart)) {

            actualEarlyNightStart = clockIn;
        }
        else {
            actualEarlyNightStart =
                    earlyNightStart;
        }


        LocalDateTime actualEarlyNightEnd;

        if (clockOut.isBefore(
                earlyNightEnd)) {

            actualEarlyNightEnd = clockOut;
        }
        else {
            actualEarlyNightEnd =
                    earlyNightEnd;
        }


        if (actualEarlyNightEnd.isAfter(
                actualEarlyNightStart)) {

            nightWorkMinutes +=
                    Duration.between(
                            actualEarlyNightStart,
                            actualEarlyNightEnd)
                            .toMinutes();
        }


        // 근태일 22:00 ~ 익일 06:00
        LocalDateTime lateNightStart =
                attendanceDate.atTime(
                        22,
                        0);

        LocalDateTime lateNightEnd =
                attendanceDate.plusDays(1)
                        .atTime(
                                6,
                                0);


        LocalDateTime actualLateNightStart;

        if (clockIn.isAfter(
                lateNightStart)) {

            actualLateNightStart = clockIn;
        }
        else {
            actualLateNightStart =
                    lateNightStart;
        }


        LocalDateTime actualLateNightEnd;

        if (clockOut.isBefore(
                lateNightEnd)) {

            actualLateNightEnd = clockOut;
        }
        else {
            actualLateNightEnd =
                    lateNightEnd;
        }


        if (actualLateNightEnd.isAfter(
                actualLateNightStart)) {

            nightWorkMinutes +=
                    Duration.between(
                            actualLateNightStart,
                            actualLateNightEnd)
                            .toMinutes();
        }


        attendanceDto.setAttendanceType(
                "정상");

        attendanceDto.setClockIn(
                Timestamp.valueOf(clockIn));

        attendanceDto.setClockOut(
                Timestamp.valueOf(clockOut));

        attendanceDto.setBreakMinutes(
                requestVO.getBreakMinutes());

        attendanceDto.setWorkDayType(
                requestVO.getWorkDayType());

        attendanceDto.setNightHours(
                nightWorkMinutes / 60.0);

        attendanceDto.setOvertimeHours(
                overtimeMinutes / 60.0);


        boolean result =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 비근무 -> 비근무
    @Override
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


        // 기존 상태가 비근무인지 확인
        if (!"결근".equals(
                attendanceDto.getAttendanceType())
                && !"유급휴가".equals(
                        attendanceDto.getAttendanceType())
                && !"무급휴가".equals(
                        attendanceDto.getAttendanceType())) {

            throw new GetOutException();
        }


        // 변경 상태도 비근무인지 확인
        if (!"결근".equals(
                requestVO.getAttendanceType())
                && !"유급휴가".equals(
                        requestVO.getAttendanceType())
                && !"무급휴가".equals(
                        requestVO.getAttendanceType())) {

            throw new GetOutException();
        }


        if (attendanceDto.getAttendanceType()
                .equals(
                        requestVO.getAttendanceType())) {

            return;
        }


        attendanceDto.setAttendanceType(
                requestVO.getAttendanceType());


        boolean result =
                employeeAttendanceDao.update(
                        attendanceDto);

        if (!result) {
            throw new GetOutException();
        }
    }


    // 기간별 근태 조회
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSearchResponseVO> search(
            long employeeNo,
            Timestamp startDate,
            Timestamp endDate) {

        if (employeeNo <= 0) {
            throw new GetOutException();
        }


        if (startDate == null
                || endDate == null) {

            throw new GetOutException();
        }


        LocalDateTime searchStart =
                startDate.toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay();

        LocalDateTime searchEnd =
                endDate.toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay();


        if (!searchStart.isBefore(
                searchEnd)) {

            throw new GetOutException();
        }


        return employeeAttendanceDao.search(
                employeeNo,
                Timestamp.valueOf(
                        searchStart),
                Timestamp.valueOf(
                        searchEnd));
    }
}