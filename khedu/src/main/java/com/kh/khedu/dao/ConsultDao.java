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

public interface ConsultDao {
	//상담 예약
	ReservationDto selectReservationOne(int reservationNo);
	List<ConsultReservationListItemVO> selectReservationList(ConsultReservationListRequestVO request);
	
	boolean reservationUpdate(int reservationNo, ConsultReservationUpdateRequestVO request);
	
	//고객 정보
	int customerSequence();
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
