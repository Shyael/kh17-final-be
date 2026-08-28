package com.kh.khedu.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.service.payroll.ContractService;

@Component
public class ContractScheduler {
	
	@Autowired
	private ContractService contractService;
	
	@Transactional
	@Scheduled(cron = "0 * * * * *"
	,zone ="Asia/Seoul")
	public void refreshContractStatus() {
		contractService.refreshContractStatus();
	}
}
