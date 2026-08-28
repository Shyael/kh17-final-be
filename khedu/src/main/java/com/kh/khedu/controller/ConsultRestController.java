package com.kh.khedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.dao.ConsultDao;
import com.kh.khedu.dto.ReservationDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.consult.ConsultReservationListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationListResponseVO;
import com.kh.khedu.vo.consult.ConsultReservationUpdateRequestVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "상담 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee/consult")
public class ConsultRestController {
	
	@Autowired
	private ConsultDao consultDao;
	
	//상담 예약 목록
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@PostMapping(value ="/reservationList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConsultReservationListResponseVO getReservationList(
			@RequestBody ConsultReservationListRequestVO request) {
		return ConsultReservationListResponseVO.builder()
					.items(consultDao.selectReservationList(request))
				.build();
	}
	
	//상담 상태 변경
	@ApiResponse(responseCode = "200", description = "변경 성공")
	@PutMapping(value = "/reservation/{reservationNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean setReservationStatus(
							@PathVariable int reservationNo,
							@RequestBody ConsultReservationUpdateRequestVO request) {
		ReservationDto findDto = consultDao.selectReservationOne(reservationNo);
		if(findDto == null) throw new TargetNotfoundException("존재하지 않는 예약정보");
		return consultDao.reservationUpdate(reservationNo, request);
	}
}
