package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.connectvo.payroll.ContractEmployeeInfoVO;
import com.kh.khedu.connectvo.payroll.ContractTeacherVO;
import com.kh.khedu.responsevo.payroll.ContractEmployeeInfoResponseVO;

public interface ContractPersonInfoDao {

    ContractEmployeeInfoVO findContractEmployeeInfo(int employeeNo);

    ContractEmployeeInfoResponseVO findContractPersonInformation(int accountNo);

    ContractTeacherVO findContractTeacher(int employeeNo);

    List<Integer> findContractTeacherSubject(int tutorNo);
}
