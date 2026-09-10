package com.kh.khedu.service;

import com.kh.khedu.vo.classSession.AdminClassSessionInsertVO;
import com.kh.khedu.vo.classSession.AdminClassSessionStatusVO;
import com.kh.khedu.vo.classSession.ClassSessionEndRequestVO;
import com.kh.khedu.vo.classSession.ClassSessionStartRequestVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

public interface ClassSessionService {
	void StartClass(ClassSessionStartRequestVO request);

	void EndClass(ClassSessionEndRequestVO request);
	
	//관리자용 강사가 [수업시작]안 누르고 지나갔을 때 사후 수동 등록
	public void insertSessionByAdmin(AdminClassSessionInsertVO request, TokenParseResponseVO parseVO);

	void updateStatusByAdmin(AdminClassSessionStatusVO request, TokenParseResponseVO parseVO);
}
