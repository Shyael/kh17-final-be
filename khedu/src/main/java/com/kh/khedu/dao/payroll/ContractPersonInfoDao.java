package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.vo.payroll.connect.ContractEmployeeInfoVO;
import com.kh.khedu.vo.payroll.connect.ContractTeacherVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeInfoResponseVO;

public interface ContractPersonInfoDao {

    ContractEmployeeInfoVO findContractEmployeeInfo(int employeeNo);

    ContractEmployeeInfoResponseVO findContractPersonInformation(int accountNo);

    ContractTeacherVO findContractTeacher(int employeeNo);

    List<Integer> findContractTeacherSubject(int tutorNo);
}
