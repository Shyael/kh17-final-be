package com.kh.khedu.service.workschedule;

import java.sql.Timestamp;

import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleAddResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleSearchResponseVO;

public interface EmployeeWorkScheduleService {

    WorkScheduleAddResponseVO add(
            WorkScheduleAddRequestVO requestVO);

    void update(
            WorkScheduleUpdateRequestVO requestVO);

    WorkScheduleResponseVO find(
            EmployeeSearchByNameVO employeeVO,
            Timestamp scheduledWorkDate);

    WorkScheduleSearchResponseVO search(
            long employeeNo,
            Timestamp startDate,
            Timestamp endDate);
    
    void autoAbsent();
}