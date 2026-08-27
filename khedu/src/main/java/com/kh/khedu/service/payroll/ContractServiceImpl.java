package com.kh.khedu.service.payroll;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.khedu.dao.payroll.ContractDao;
import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.requestvo.payroll.ContractAddRequestVO;
import com.kh.khedu.requestvo.payroll.ContractChangeConditionRequestVO;
import com.kh.khedu.requestvo.payroll.ContractEmployeeSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractEmployerSignRequestVO;
import com.kh.khedu.requestvo.payroll.ContractUpdateDraftRequestVO;
import com.kh.khedu.responsevo.payroll.ContractSignResponseVO;
import com.kh.khedu.util.SignatureEncryptor;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;

@Service
public class ContractServiceImpl implements ContractService {

	@Autowired
	private ContractDao contractDao;

	@Autowired
	private SignatureEncryptor signatureEncryptor;

	// 근로계약 등록 //권한 설정 완
	@Transactional
	@Override
	public ContractDto add(ContractAddRequestVO request, TokenParseResponseVO parseVO) {

		List<String> permission = parseVO.getRoleNames();
		
		if(!permission.contains("admin")) throw new GetOutException();
		
		
		// [1] 계약 대상 직원의 현재 계약 확인
		ContractDto currentContract = contractDao.findCurrent(request.getEmployeeNo());

		// [2] 현재 계약이 있으면 신규 계약 작성 불가
		// 기존 계약 조건 변경은 changeWorkCondition에서 처리
		if (currentContract != null)
			throw new GetOutException();

		// [3] 요청정보를 근로계약 DTO로 변환
		ContractDto contractDto = new ContractDto();
		BeanUtils.copyProperties(request, contractDto);

		// [4] 계약기간 확인
		if (contractDto.getContractEnd() != null
				&& contractDto.getContractEnd().before(contractDto.getContractStart())) {
			throw new GetOutException();
		}

		// [5] 소정근로시간 확인
		if (contractDto.getDailyWorkHours() > contractDto.getWeeklyWorkHours()) {
			throw new GetOutException();
		}

		// [6] 최초 등록 상태는 서명대기
		contractDto.setContractStatus("pending");

		// [7] 근로계약 번호 생성
		long contractNo = contractDao.contractSequence();

		contractDto.setContractNo(contractNo);

		// [8] 신규 계약은 서명 전 상태로 생성
		contractDto.setEmployeeSignature(null);
		contractDto.setEmployerSignature(null);
		contractDto.setSignedTime(null);

		// [9] 신규 근로계약 등록
		contractDao.contractAdd(contractDto);

		return contractDto;
	}

	// 양측 서명 완료 전 근로계약 내용 수정 //권한 설정 완
	@Override
	public void updateDraft(long contractNo, ContractUpdateDraftRequestVO request, TokenParseResponseVO parseVO) {

		
	List<String> permission = parseVO.getRoleNames();
		
		if(!permission.contains("admin")) throw new GetOutException();
		
		// [1] 계약 조회
		ContractDto currentContract = contractDao.find(contractNo);

		if (currentContract == null)
			throw new TargetNotfoundException();

		// 원장인지 해당 직원인지 확인(추후 작성)

		// [2] 양측 서명이 모두 작성되었거나
		// 이미 체결완료된 계약이면 수정 불가
		if ((currentContract.getEmployeeSignature() != null && currentContract.getEmployerSignature() != null)
				|| currentContract.getSignedTime() != null) {
			throw new GetOutException();
		}

		// [3] 아직 체결 전 계약만 수정 가능
		if (!"pending".equals(currentContract.getContractStatus()))
			throw new GetOutException();

		// [4] 요청정보 적용
		BeanUtils.copyProperties(request, currentContract);

		// [5] 계약기간 확인
		if (currentContract.getContractEnd() != null
				&& currentContract.getContractEnd().before(currentContract.getContractStart())) {
			throw new GetOutException();
		}
		// [7] 새 소정근로시간 확인
		if (currentContract.getDailyWorkHours() > currentContract.getWeeklyWorkHours()) {
			throw new GetOutException();
		}

		// [6] 계약내용 수정
		boolean result = contractDao.updateDraft(request);

		if (result == false)
			throw new GetOutException();
	}

	// 을(직원) 서명 //권한 설정 완
	@Transactional
	@Override
	public void employeeSign(long contractNo, ContractEmployeeSignRequestVO request, TokenParseResponseVO parseVO) {
		// 권한은 을만
		
		
		// 원장 내보내기
		List<String> permission = parseVO.getRoleNames();
		
		if(permission.contains("admin")) throw new GetOutException();

	

		// "을"을 검증
		int employeeNo = parseVO.getAccountNo();

		ContractDto find = contractDao.find(contractNo);
		if (find.getContractNo() != employeeNo)
			throw new GetOutException();

		// [1] 서명정보 조회
		ContractDto currentContract = contractDao.findSignature(contractNo);

		if (currentContract == null)
			throw new TargetNotfoundException();

		// [2] 이미 양측 서명이 완료된 계약이면 수정 불가
		if (currentContract.getSignedTime() != null)
			throw new GetOutException();

		// 원장인지 해당 직원인지 확인(추후 작성)

		// [3] 직원 서명 설정
		currentContract.setEmployeeSignature(request.getEmployeeSignature());

		// [4] 직원 서명 저장
		// 암호화는 DAO에서 처리
		boolean result = contractDao.employeeSign(currentContract);

		if (result == false)
			throw new GetOutException();

		// [5] 원장 서명이 이미 존재하면
		// 이번 직원 서명으로 양측 서명 완료
		if (currentContract.getEmployerSignature() != null) {

			currentContract.setSignedTime(Timestamp.valueOf(LocalDateTime.now()));

			currentContract.setContractStatus("scheduled");

			boolean completeResult = contractDao.completeSign(currentContract);

			if (completeResult == false)
				throw new GetOutException();
		}
	}

	// 갑(원장) 서명 //권한 설정 완
	@Transactional
	@Override
	public void employerSign(long contractNo, ContractEmployerSignRequestVO request, TokenParseResponseVO parseVO) {
		

		// [1] 서명정보 조회
		ContractDto currentContract = contractDao.findSignature(contractNo);

		if (currentContract == null)
			throw new TargetNotfoundException();

		// [2] 이미 양측 서명이 완료된 계약이면 수정 불가
		if (currentContract.getSignedTime() != null)
			throw new GetOutException();

		// 원장인지 확인(추후 작성)

		// [3] 원장 서명 설정
		currentContract.setEmployerSignature(request.getEmployerSignature());

		// [4] 원장 서명 저장
		// 암호화는 DAO에서 처리
		boolean result = contractDao.employerSign(currentContract);

		if (result == false)
			throw new GetOutException();

		// [5] 직원 서명이 이미 존재하면
		// 이번 원장 서명으로 양측 서명 완료
		if (currentContract.getEmployeeSignature() != null) {

			currentContract.setSignedTime(Timestamp.valueOf(LocalDateTime.now()));

			currentContract.setContractStatus("scheduled");

			boolean completeResult = contractDao.completeSign(currentContract);

			if (completeResult == false)
				throw new GetOutException();
		}
	}

	// 현재 근로계약 조회
	@Override
	public ContractDto findCurrent(long employeeNo) {

		ContractDto contractDto = contractDao.findCurrent(employeeNo);

		if (contractDto == null)
			throw new TargetNotfoundException();

		return contractDto;
	}

	// 과거 근로계약 조회
	@Override
	public List<ContractDto> findPast(long employeeNo) {

		List<ContractDto> history = contractDao.findPast(employeeNo);

		if (history.size() == 0)
			throw new TargetNotfoundException();

		return history;
	}

	// 직원의 전체 근로계약 조회
	@Override
	public List<ContractDto> findAllByEmployee(long employeeNo) {

		List<ContractDto> contracts = contractDao.findAllByEmployee(employeeNo);
		return contracts;
	}

	// 근로계약 종료
	@Transactional
	@Override
	public void endContract(long contractNo) {

		// [1] 종료할 근로계약 조회
		ContractDto contractDto = contractDao.findPeriodAndStatus(contractNo);

		if (contractDto == null)
			throw new TargetNotfoundException();

		// [2] 이미 종료된 계약이면 종료 불가
		if ("ended".equals(contractDto.getContractStatus()))
			throw new GetOutException();

		// [3] 계약 종료일 및 상태 설정
		contractDto.setContractEnd(Timestamp.valueOf(LocalDateTime.now()));

		contractDto.setContractStatus("ended");

		// [4] 근로계약 종료 처리
		boolean result = contractDao.changeStatus(contractDto);

		if (result == false)
			throw new GetOutException();
	}

	// 체결 후 근로조건 변경
	@Transactional
	@Override
	public ContractDto changeWorkCondition(long contractNo, ContractChangeConditionRequestVO request) {

		// [1] 기존 계약 전체 조회
		ContractDto originDto = contractDao.find(contractNo);

		if (originDto == null)
			throw new TargetNotfoundException();

		// [2] 체결 완료된 계약인지 확인
		if (originDto.getSignedTime() == null)
			throw new GetOutException();

		// [3] 이미 종료된 계약은 변경 불가
		if ("ended".equals(originDto.getContractStatus()))
			throw new GetOutException();

		// [4] 새로운 근로조건 DTO 생성
		ContractDto newContractDto = new ContractDto();

		BeanUtils.copyProperties(request, newContractDto);

		// [5] 기존 계약의 직원을 새 계약에 연결
		newContractDto.setEmployeeNo(originDto.getEmployeeNo());

		// [6] 새 계약기간 확인
		if (newContractDto.getContractEnd() != null
				&& newContractDto.getContractEnd().before(newContractDto.getContractStart())) {
			throw new GetOutException();
		}

		// [7] 새 소정근로시간 확인
		if (newContractDto.getDailyWorkHours() > newContractDto.getWeeklyWorkHours()) {
			throw new GetOutException();
		}

		// [7] 변경 계약은 미래 시점부터 적용
		Timestamp current = Timestamp.valueOf(LocalDateTime.now());

		if (!newContractDto.getContractStart().after(current))
			throw new GetOutException();

		// [8] 기존 계약은 새 계약 시작일까지 유지
		originDto.setContractEnd(newContractDto.getContractStart());

		boolean changeResult = contractDao.changeStatus(originDto);

		if (changeResult == false)
			throw new GetOutException();

		// [9] 새 계약은 다시 서명대기 상태로 생성
		newContractDto.setContractStatus("pending");

		// [10] 새 계약번호 생성
		long newContractNo = contractDao.contractSequence();

		newContractDto.setContractNo(newContractNo);

		// [11] 새 계약 서명 초기화
		newContractDto.setEmployeeSignature(null);
		newContractDto.setEmployerSignature(null);
		newContractDto.setSignedTime(null);

		// [12] 변경된 근로조건으로 새 계약 등록
		contractDao.contractAdd(newContractDto);

		return newContractDto;
	}

	// 서명정보 조회
	@Override
	public ContractSignResponseVO findSignature(long contractNo, TokenParseResponseVO parseVO) {

		ContractDto contractDto = contractDao.findSignature(contractNo);

		if (contractDto == null)
			throw new TargetNotfoundException();

		// 권한 확인 작업 추후 필요

		// 서명하지 않은 쪽은 null 유지
		String employeeSignature = null;
		String employerSignature = null;

		if (contractDto.getEmployeeSignature() != null) {

			employeeSignature = signatureEncryptor.decrypt(contractDto.getEmployeeSignature());
		}

		if (contractDto.getEmployerSignature() != null) {

			employerSignature = signatureEncryptor.decrypt(contractDto.getEmployerSignature());
		}

		return ContractSignResponseVO.builder().contractNo(contractDto.getContractNo())
				.employeeSignature(employeeSignature).employerSignature(employerSignature)
				.signedTime(contractDto.getSignedTime()).build();
	}

	// 계약기간에 따른 상태 갱신
	@Transactional
	@Override
	public void refreshContractStatus(long contractNo) {

		// [1] 종료일이 도래한 기존 계약 종료
		contractDao.endContracts(contractNo);

		// [2] 시작일이 도래한 체결완료 계약 활성화
		contractDao.activateContracts(contractNo);
	}

	@Transactional
	@Override
	public void exitContract(long contractNo) {

		contractDao.exitContracts(contractNo);
	}

}