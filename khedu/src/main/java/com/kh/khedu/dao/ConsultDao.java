package com.kh.khedu.dao;

import java.util.List;

import com.kh.khedu.dto.ReservationDto;
import com.kh.khedu.vo.consult.ConsultReservationListItemVO;
import com.kh.khedu.vo.consult.ConsultReservationListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationUpdateRequestVO;

public interface ConsultDao {
	ReservationDto selectReservationOne(int reservationNo);
	List<ConsultReservationListItemVO> selectReservationList(ConsultReservationListRequestVO request);
	
	boolean reservationUpdate(int reservationNo, ConsultReservationUpdateRequestVO request);
}
