package com.kh.khedu.service.payroll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.payroll.ContractPersonInfoDao;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.payroll.connect.ContractEmployeeInfoVO;
import com.kh.khedu.vo.payroll.connect.ContractTeacherVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeDeskResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeInfoResponseVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeTeacherResponseVO;
import com.kh.khedu.vo.payroll.response.ContractTeacherResponseVO;

@Service
public class ContractPersonInfoServiceImpl implements ContractPersonInfoService {

    @Autowired
    private ContractPersonInfoDao contractPersonInfoDao;

    @Override
    public ContractEmployeeDeskResponseVO findDesk(int employeeNo) {

        ContractEmployeeInfoVO employeeInfo =
                contractPersonInfoDao.findContractEmployeeInfo(employeeNo);

        if(employeeInfo == null) {
            throw new TargetNotfoundException();
        }

        ContractEmployeeInfoResponseVO personInfo =
                contractPersonInfoDao.findContractPersonInformation(
                        employeeInfo.getAccountNo()
                );

        if(personInfo == null) {
            throw new TargetNotfoundException();
        }

        return ContractEmployeeDeskResponseVO.builder()
                .employeeStatus(employeeInfo.getEmployeeStatus())
                .employeeType(employeeInfo.getEmployeeType())
                .accountName(personInfo.getAccountName())
                .accountPhone(personInfo.getAccountPhone())
                .build();
    }

    @Override
    public ContractEmployeeTeacherResponseVO findTeacher(int employeeNo) {

        ContractEmployeeInfoVO employeeInfo =
                contractPersonInfoDao.findContractEmployeeInfo(employeeNo);

        if(employeeInfo == null) {
            throw new TargetNotfoundException();
        }

        if(!"강사".equals(employeeInfo.getEmployeeType())) {
            throw new TargetNotfoundException();
        }

        ContractEmployeeInfoResponseVO personInfo =
                contractPersonInfoDao.findContractPersonInformation(
                        employeeInfo.getAccountNo()
                );

        if(personInfo == null) {
            throw new TargetNotfoundException();
        }

        ContractTeacherVO teacher =
                contractPersonInfoDao.findContractTeacher(employeeNo);

        if(teacher == null) {
            throw new TargetNotfoundException();
        }

        ContractTeacherResponseVO teacherSubject =
                ContractTeacherResponseVO.builder()
                        .tutorSubjectNoList(
                                contractPersonInfoDao.findContractTeacherSubject(
                                        teacher.getTutorNo()
                                )
                        )
                        .build();

        return ContractEmployeeTeacherResponseVO.builder()
                .employeeStatus(employeeInfo.getEmployeeStatus())
                .employeeType(employeeInfo.getEmployeeType())
                .accountName(personInfo.getAccountName())
                .accountPhone(personInfo.getAccountPhone())
                .teacherSubject(teacherSubject)
                .build();
    }
}