package com.kh.khedu.dao.payroll;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.payroll.connect.ContractEmployeeInfoVO;
import com.kh.khedu.vo.payroll.connect.ContractTeacherVO;
import com.kh.khedu.vo.payroll.response.ContractEmployeeInfoResponseVO;
@Repository
public class ContractPersonInfoDaoMybatis implements ContractPersonInfoDao {

	  @Autowired
	    private SqlSession sqlSession;

	    @Override
	    public ContractEmployeeInfoVO findContractEmployeeInfo(int employeeNo) {
	        return sqlSession.selectOne(
	                "mapper.payroll.findContractEmployeeInfo",
	                employeeNo
	        );
	    }

	    @Override
	    public ContractEmployeeInfoResponseVO findContractPersonInformation(int accountNo) {
	        return sqlSession.selectOne(
	                "mapper.payroll.findContractPersonInformation",
	                accountNo
	        );
	    }

	    @Override
	    public ContractTeacherVO findContractTeacher(int employeeNo) {
	        return sqlSession.selectOne(
	                "mapper.payroll.findContractTeacher",
	                employeeNo
	        );
	    }

	    @Override
	    public List<Integer> findContractTeacherSubject(int tutorNo) {
	        return sqlSession.selectList(
	                "mapper.payroll.findContractTeacherSubject",
	                tutorNo
	        );
	    }
}
