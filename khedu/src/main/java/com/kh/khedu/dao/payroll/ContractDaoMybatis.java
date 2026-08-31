package com.kh.khedu.dao.payroll;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.payroll.ContractDto;
import com.kh.khedu.util.SignatureEncryptor;
import com.kh.khedu.vo.payroll.request.ContractChangeConditionRequestVO;
import com.kh.khedu.vo.payroll.request.ContractUpdateDraftRequestVO;

@Repository
public class ContractDaoMybatis implements ContractDao {

	@Autowired
	private SqlSession sqlSession;

	@Autowired
	private SignatureEncryptor signatureEncryptor;


	// 근로계약 번호 생성
	@Override
	public long contractSequence() {
		return sqlSession.selectOne(
			"mapper.payroll.contractSequence"
		);
	}


	// 근로계약 초안 등록
	@Override
	public void contractAdd(ContractDto contractDto) {
		sqlSession.insert(
			"mapper.payroll.contractAdd",
			contractDto
		);
	}


	// 계약번호로 근로계약 전체 조회
	@Override
	public ContractDto find(long contractNo) {
		return sqlSession.selectOne(
			"mapper.payroll.find",
			contractNo
		);
	}


	// 직원의 현재 근로계약 조회
	@Override
	public ContractDto findCurrent(long employeeNo) {
		return sqlSession.selectOne(
			"mapper.payroll.findCurrent",
			employeeNo
		);
	}


	// 직원의 과거(종료) 근로계약 조회
	@Override
	public List<ContractDto> findPast(long employeeNo) {
		return sqlSession.selectList(
			"mapper.payroll.findPast",
			employeeNo
		);
	}


	// 직원의 전체 근로계약 조회
	@Override
	public List<ContractDto> findAllByEmployee(long employeeNo) {
		return sqlSession.selectList(
			"mapper.payroll.findAllByEmployee",
			employeeNo
		);
	}


	// 계약의 급여조건 조회
	@Override
	public ContractDto findWageCondition(long contractNo) {
		return sqlSession.selectOne(
			"mapper.payroll.findWageCondition",
			contractNo
		);
	}


	// 계약의 근로시간 조건 조회
	@Override
	public ContractDto findWorkTimeCondition(long contractNo) {
		return sqlSession.selectOne(
			"mapper.payroll.findWorkTimeCondition",
			contractNo
		);
	}


	// 계약기간 및 계약상태 조회
	@Override
	public ContractDto findPeriodAndStatus(long contractNo) {
		return sqlSession.selectOne(
			"mapper.payroll.findPeriodAndStatus",
			contractNo
		);
	}


	// 급여 지급예정일 조회
	@Override
	public Integer findPayday(long contractNo) {
		return sqlSession.selectOne(
			"mapper.payroll.findPayday",
			contractNo
		);
	}


	// 계약 서명정보 조회
	@Override
	public ContractDto findSignature(long contractNo) {
		return sqlSession.selectOne(
			"mapper.payroll.findSignature",
			contractNo
		);
	}


	// 양측 서명 완료 전 계약내용 수정
	@Override
	public boolean updateDraft(ContractUpdateDraftRequestVO request) {
		return sqlSession.update(
			"mapper.payroll.updateDraft",
			request
		) > 0;
	}
	
	// 서명 후 근무 조건 계약 내용 수정
	@Override
	public boolean changeContractCondition(ContractChangeConditionRequestVO request) {
		return sqlSession.update("mapper.payroll.update",request)>0;
	}

	// 을(직원) 서명
	@Override
	public boolean employeeSign(ContractDto contractDto) {

		// [1] 직원 서명 암호화
		String encrypted =
				signatureEncryptor.encrypt(
					contractDto.getEmployeeSignature()
				);

		contractDto.setEmployeeSignature(encrypted);


		// [2] 암호화된 서명 저장
		return sqlSession.update(
			"mapper.payroll.employeeSign",
			contractDto
		) > 0;
	}


	// 갑(원장) 서명
	@Override
	public boolean employerSign(ContractDto contractDto) {

		// [1] 원장 서명 암호화
		String encrypted =
				signatureEncryptor.encrypt(
					contractDto.getEmployerSignature()
				);

		contractDto.setEmployerSignature(encrypted);


		// [2] 암호화된 서명 저장
		return sqlSession.update(
			"mapper.payroll.employerSign",
			contractDto
		) > 0;
	}


	// 양측 서명 완료 처리
	@Override
	public boolean completeSign(ContractDto contractDto) {
		return sqlSession.update(
			"mapper.payroll.completeSign",
			contractDto
		) > 0;
	}

	
	//계약 연장(기간만)
	@Override
	public boolean extendContract(
	        ContractDto contractDto) {

	    return sqlSession.update(
	            "mapper.payroll.extendContract",
	            contractDto
	    ) > 0;
	}


	// 시작일이 도래한 체결완료 계약 활성화
	@Override
	public boolean activateContracts() {
		return sqlSession.update(
			"mapper.payroll.activateContracts")>0
		;
	}


	// 종료일이 도래한 계약 종료
	@Override
	public boolean endContracts() {
		return sqlSession.update(
			"mapper.payroll.endContracts")>0
		;
	}

	//도중 퇴사
	@Override
	public boolean exitContracts(long contractNo) {
		return sqlSession.update("mapper.payroll.exitContracts")>0;
	}

	@Override
	public Integer findEmployeeNoByAccountNo(long accountNo) {
	    return sqlSession.selectOne(
	            "mapper.payroll.findEmployeeNoByAccountNo",
	            accountNo
	    );
	}
}