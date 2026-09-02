package com.kh.khedu.dao.payroll;

import java.sql.Timestamp;
import java.util.List;

import com.kh.khedu.dto.payroll.EmployeeWorkScheduleDto;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleResponseVO;

public interface EmployeeWorkScheduleDao {
	long sequence();

	boolean add(WorkScheduleAddRequestVO request);

	EmployeeWorkScheduleDto findByNo(
	        long workScheduleNo);
	
	boolean update(WorkScheduleUpdateRequestVO request);

	EmployeeWorkScheduleDto find(
	        EmployeeSearchByNameVO employeeVO,
	        Timestamp scheduledWorkDate);
	List<WorkScheduleResponseVO> search(
	        long employeeNo,
	        Timestamp startDate,
	        Timestamp endDate);

	EmployeeWorkScheduleDto isExist(
			int employeeNo, Timestamp scheduledWorkDate
			);
	
	List<EmployeeWorkScheduleDto> findAutoAbsentTarget(
	        Timestamp now);
	
	EmployeeWorkScheduleDto findByContract(
	        long contractNo,
	        Timestamp scheduledWorkDate);

}
