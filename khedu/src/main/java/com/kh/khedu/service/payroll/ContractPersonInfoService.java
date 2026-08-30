package com.kh.khedu.service.payroll;

import com.kh.khedu.responsevo.payroll.ContractEmployeeDeskResponseVO;
import com.kh.khedu.responsevo.payroll.ContractEmployeeTeacherResponseVO;

public interface ContractPersonInfoService {

    ContractEmployeeDeskResponseVO findDesk(int employeeNo);

    ContractEmployeeTeacherResponseVO findTeacher(int employeeNo);
}