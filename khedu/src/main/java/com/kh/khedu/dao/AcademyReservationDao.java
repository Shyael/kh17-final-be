package com.kh.khedu.dao;

import com.kh.khedu.vo.consult.ConsultReservationInsertRequestVO;

public interface AcademyReservationDao {
	int sequence();
	boolean insertReservation(ConsultReservationInsertRequestVO request);
}
