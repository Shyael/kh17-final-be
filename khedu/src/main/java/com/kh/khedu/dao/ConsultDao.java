package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ConsultCustomerDto;
import com.kh.khedu.dto.ConsultDto;
import com.kh.khedu.dto.ReservationDto;
import com.kh.khedu.vo.consult.ConsultCustomerListItemVO;
import com.kh.khedu.vo.consult.ConsultCustomerListRequestVO;
import com.kh.khedu.vo.consult.ConsultListItemVO;
import com.kh.khedu.vo.consult.ConsultListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationListItemVO;
import com.kh.khedu.vo.consult.ConsultReservationListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationUpdateRequestVO;
import com.kh.khedu.vo.consult.ConsultUpdateRequestVO;
import com.kh.khedu.vo.dashboard.DashboardReservationVO;

public interface ConsultDao {
	//상담 예약
	ReservationDto selectReservationOne(int reservationNo);
	int selectReservationListCount(ConsultReservationListRequestVO request);
	List<ConsultReservationListItemVO> selectReservationList(ConsultReservationListRequestVO request);
	
	// 원장/데스크 대시보드 - 최근 신규 상담 5개
	List<DashboardReservationVO> selectRecentReservationList();

	// 원장/데스크 대시보드 - 오늘 상담 5개
	List<DashboardReservationVO> selectTodayReservationList();
	
	// 오늘 상담갯수
	int countTodayReservations();
	
	boolean reservationUpdate(int reservationNo, ConsultReservationUpdateRequestVO request);
	
	//고객 정보
	int customerSequence();
	Integer selectConsultCustomerNo(String customerName, String customerMobile);
	ConsultCustomerDto selectConsultCustomerOne(int customerNo);
	ConsultCustomerDto selectConsultCustomerDupCheck(ConsultCustomerListItemVO request);
	List<ConsultCustomerListItemVO> selectConsultCustomerList(ConsultCustomerListRequestVO request);
	
	boolean customerAdd(ConsultCustomerListItemVO request);
	boolean customerUpdate(ConsultCustomerListItemVO request);
	
	//상담 정보
	int consultSequence();
	ConsultDto selectConsultOne(int consultNo);
	List<ConsultListItemVO> selectConsultList(ConsultListRequestVO request);
	
	boolean consultAdd(ConsultUpdateRequestVO request);
	boolean consultUpdate(ConsultUpdateRequestVO request);
}