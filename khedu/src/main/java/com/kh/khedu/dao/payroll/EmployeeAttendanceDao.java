package com.kh.khedu.dao.payroll;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.payroll.response.AttendanceFindVO;
import com.kh.khedu.vo.payroll.response.AttendanceSearchResponseVO;

public interface EmployeeAttendanceDao {

    long sequence();

    boolean add(EmployeeAttendanceDto dto);

    boolean update(EmployeeAttendanceDto dto);

    EmployeeAttendanceDto find(AttendanceFindVO findVO);

    List<AttendanceSearchResponseVO> search(
            long employeeNo,
            Timestamp startDate,
            Timestamp endDate);
    
    EmployeeDetailVO findByAccountNo(int accountNo);
}
