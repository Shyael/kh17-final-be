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
import com.kh.khedu.dto.ConsultCustomerDto;
import com.kh.khedu.dto.ConsultDto;
import com.kh.khedu.dto.ReservationDto;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.consult.ConsultCustomerListItemVO;
import com.kh.khedu.vo.consult.ConsultCustomerListRequestVO;
import com.kh.khedu.vo.consult.ConsultCustomerListResponseVO;
import com.kh.khedu.vo.consult.ConsultCustomerUpdateResponseVO;
import com.kh.khedu.vo.consult.ConsultListRequestVO;
import com.kh.khedu.vo.consult.ConsultListResponseVO;
import com.kh.khedu.vo.consult.ConsultReservationListRequestVO;
import com.kh.khedu.vo.consult.ConsultReservationListResponseVO;
import com.kh.khedu.vo.consult.ConsultReservationUpdateRequestVO;
import com.kh.khedu.vo.consult.ConsultUpdateRequestVO;
import com.kh.khedu.vo.consult.ConsultUpdateResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Tag(name = "상담 정보 관리 서비스")
@RestController
@RequestMapping("/api/employee/consult")
public class ConsultRestController {
	
	@Autowired
	private ConsultDao consultDao;
	
	//상담 예약 목록
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@PostMapping(value ="/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConsultReservationListResponseVO getReservationList(
			@RequestBody ConsultReservationListRequestVO request) {
		return ConsultReservationListResponseVO.builder()
					.items(consultDao.selectReservationList(request))
				.build();
	}
	
	//상담 예약 상태 변경
	@ApiResponse(responseCode = "200", description = "변경 성공")
	@PutMapping(value = "/reservation/{reservationNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean setReservationStatus(
							@PathVariable int reservationNo,
							@RequestBody ConsultReservationUpdateRequestVO request) {
		ReservationDto findDto = consultDao.selectReservationOne(reservationNo);
		if(findDto == null) throw new TargetNotfoundException("존재하지 않는 예약정보");
		return consultDao.reservationUpdate(reservationNo, request);
	}
	
	//상담 고객 목록
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@PostMapping(value ="/customer", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConsultCustomerListResponseVO getConsultCustomerList(
			@RequestBody ConsultCustomerListRequestVO request
			) {
		return ConsultCustomerListResponseVO.builder()
					.items(consultDao.selectConsultCustomerList(request))
				.build();
	}
	
	//상담 정보 저장
	@ApiResponse(responseCode = "200", description = "저장 성공")
	@PutMapping(value ="/customer", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConsultCustomerUpdateResponseVO setConsultCustomerUpdate(
			@RequestBody ConsultCustomerListItemVO request
			) {
		try {
			//연락처 중복 체크
			ConsultCustomerDto checkDto = consultDao.selectConsultCustomerDupCheck(request);
			if(checkDto != null) 
				return ConsultCustomerUpdateResponseVO.builder()
							.result(false)
							.errMsg("이미 사용중인 연락처입니다")
							.item(null)
						.build();
			//필수값 체크
			if(request.getStudentName().length() == 0 || request.getStudentPhone().length() == 0)
				return ConsultCustomerUpdateResponseVO.builder()
						.result(false)
						.errMsg("이름과 연락처는 필수입력입니다")
						.item(null)
					.build();
				
			if(request.getCustomerNo() == null) {
				//신규등록
	 			request.setCustomerNo(consultDao.customerSequence());
	 			return ConsultCustomerUpdateResponseVO.builder()
	 						.result(consultDao.customerAdd(request))
	 						.item(request)
	 					.build();
			} else {
				//정보수정
				ConsultCustomerDto findDto = consultDao.selectConsultCustomerOne(request.getCustomerNo());
				if(findDto == null) throw new TargetNotfoundException("고객 정보 없음");
				return ConsultCustomerUpdateResponseVO.builder()
							.result(consultDao.customerUpdate(request))
							.item(request)
						.build();
			}
		} catch(Exception e) {
			return ConsultCustomerUpdateResponseVO.builder()
					.result(false)
					.errMsg("일시적인 오류가 발생했습니다")
					.item(null)
				.build();
		}
	}
	
	//상담 내역 조회
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@PostMapping(value ="/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConsultListResponseVO getConsultList(
			@RequestBody ConsultListRequestVO request) {
		return ConsultListResponseVO.builder()
					.items(consultDao.selectConsultList(request))
				.build();
	}
	
	//상담 내용 저장
	@ApiResponse(responseCode = "200", description = "저장 성공")
	@PutMapping(value ="/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ConsultUpdateResponseVO setConsultUpdate(
			@RequestBody ConsultUpdateRequestVO request
			) {
		try {
			//필수값 체크
			if(request.getConsultTitle().length() == 0 || request.getConsultContent().length() == 0)
				return ConsultUpdateResponseVO.builder()
							.result(false)
							.errMsg("제목과 내용은 필수입력입니다")
							.consultNo(null)
						.build();
			
			if(request.getConsultNo() == null) {
				//신규등록
	 			request.setConsultNo(consultDao.consultSequence());
	 			return ConsultUpdateResponseVO.builder()
	 						.result(consultDao.consultAdd(request))
	 						.consultNo(request.getConsultNo())
	 					.build();
			} else {
				//정보수정
				ConsultDto findDto = consultDao.selectConsultOne(request.getConsultNo());
				if(findDto == null) throw new TargetNotfoundException("상담 정보 없음");
				return ConsultUpdateResponseVO.builder()
							.result(consultDao.consultUpdate(request))
							.consultNo(request.getConsultNo())
						.build();
			}
		} catch(Exception e) {
			log.error(e.getMessage());
			return ConsultUpdateResponseVO.builder()
						.result(false)
						.errMsg("일시적인 오류가 발생했습니다")
						.consultNo(null)
					.build();
		}
	}
}
