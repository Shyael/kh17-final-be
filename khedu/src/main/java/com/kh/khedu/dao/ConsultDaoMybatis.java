package com.kh.khedu.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.ConsultCustomerDto;
import com.kh.khedu.dto.ReservationDto;
import com.kh.khedu.vo.consult.ConsultCustomerListItemVO;
import com.kh.khedu.vo.consult.ConsultCustomerListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationListItemVO;
import com.kh.khedu.vo.consult.ConsultReservationListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationUpdateRequestVO;

@Repository
public class ConsultDaoMybatis implements ConsultDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public ReservationDto selectReservationOne(int reservationNo) {
		return sqlSession.selectOne("mapper.consult.reservationInfo", reservationNo);
	}
	
	@Override
	public List<ConsultReservationListItemVO> selectReservationList(ConsultReservationListRequestVO request) {
		List<ReservationDto> dtoList = sqlSession.selectList("mapper.consult.reservationList", request);
		List<ConsultReservationListItemVO> voList = new ArrayList<>();
		for(ReservationDto dto : dtoList) {
			ConsultReservationListItemVO vo = ConsultReservationListItemVO.builder()
						.reservationNo(dto.getReservationNo())
						.reservationName(dto.getReservationName())
						.reservationPhone(dto.getReservationPhone())
						.reservationType(dto.getReservationType())
						.reservationTime(dto.getReservationTime())
						.reservationStatus(dto.getReservationStatus())
						.reservationStatusString(dto.getReservationStatusString())
						.reservationComment(dto.getReservationComment())
						.reservationCtime(dto.getReservationCtime())
					.build();
			voList.add(vo);
		}		
		return voList;
	}
	
	@Override
	public boolean reservationUpdate(int reservationNo, ConsultReservationUpdateRequestVO request) {
		Map<String, Object> params = new HashMap<>();
		params.put("reservationNo", reservationNo);
		params.put("reservationStatus", request.getReservationStatus());
		params.put("reservationComment", request.getReservationComment());
		return sqlSession.update("mapper.consult.reservationUpdate", params) > 0;
	}
	
	@Override
	public int customerSequence() {
		return sqlSession.selectOne("mapper.consult.customerSequence");
	}
	
	@Override
	public ConsultCustomerDto selectConsultCustomerOne(int customerNo) {
		return sqlSession.selectOne("mapper.consult.customerInfo", customerNo);
	}
	
	@Override
	public List<ConsultCustomerListItemVO> selectConsultCustomerList(ConsultCustomerListRequestVO request) {
		return sqlSession.selectList("mapper.consult.customerList", request);
	}
	
	@Override
	public boolean customerAdd(ConsultCustomerListItemVO request) {
		return sqlSession.update("mapper.consult.customerAdd", request) > 0;
	}
	
	@Override
	public boolean customerUpdate(ConsultCustomerListItemVO request) {
		return sqlSession.update("mapper.consult.customerUpdate", request) > 0;
	}
}
