package com.kh.khedu.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.vo.payroll.request.DashboardContractExpiringVO;
import com.kh.khedu.vo.payroll.request.DashboardPayrollDueQueryVO;
import com.kh.khedu.vo.payroll.request.DashboardPendingContractVO;
@Repository
public class AdminDashboardDaoMybatis implements AdminDashboardDao {

	@Autowired
	private SqlSession sqlSession;
	
	
	@Override
	public List<DashboardPendingContractVO> findPendingContractList() {
	    return sqlSession.selectList(
	            "mapper.adminDashboard.findPendingContractList"
	    );
	}

	@Override
	public List<DashboardContractExpiringVO> findContractExpiringList() {
	    return sqlSession.selectList(
	            "mapper.adminDashboard.findContractExpiringList"
	    );
	}

	@Override
	public List<DashboardPayrollDueQueryVO> getPayrollDueList() {
	    return sqlSession.selectList(
	            "mapper.adminDashboard.getPayrollDueList"
	    );
	}

}
