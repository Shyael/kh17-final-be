package com.kh.khedu.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.AssignmentDao;
import com.kh.khedu.dao.ExamDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionSchedulerService {
    @Autowired
    private AssignmentDao assignmentDao;
    @Autowired
    private ExamDao examDao;
    
    
    //제출기한 지나면 마감처리
    @Scheduled(
	    cron = "0 0 0,12 * * *",
	    zone = "Asia/Seoul"
	)
	@Transactional
	public void closeExpiredContents() {
	    int assignmentCount = assignmentDao.closeExpiredAssignments();

	    int examCount = examDao.closeExpiredExams();

	    log.info(
	        "[스케줄러] 자동 마감 - 과제 {}건 / 시험 {}건",
	        assignmentCount,
	        examCount
	    );
	}
    
}
