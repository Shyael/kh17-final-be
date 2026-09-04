package com.kh.khedu.service.payroll;

import com.kh.khedu.vo.payroll.response.ContractEmployeeDeskResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeTeacherResponseVO;

public interface ContractPersonInfoService {

    ContractEmployeeDeskResponseVO findDesk(int employeeNo);

    ContractEmployeeTeacherResponseVO findTeacher(int employeeNo);
}