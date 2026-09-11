package com.kh.khedu.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kh.khedu.dao.EmployeeDao;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
@Component
public class PrincipalChecker {
	@Autowired
	private EmployeeDao employeeDao;
	public void CheckYou(TokenParseResponseVO parseVO, int employeeNo) {
		
		boolean areYouOwner = employeeDao.checkEmployeeOwner(parseVO.getAccountNo(), employeeNo);
		if(!areYouOwner) throw new YouAreNotMeException();
	}
}
