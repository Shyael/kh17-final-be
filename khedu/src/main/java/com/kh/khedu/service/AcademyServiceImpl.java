package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dao.AcademyDao;
import com.kh.khedu.dao.AcademyHistoryDao;
import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dto.AcademyDto;
import com.kh.khedu.dto.AcademyHistoryDto;
import com.kh.khedu.dto.AcademySubjectDto;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.error.AlreadyExistsException;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.academy.AcademyDetailResponseVO;

@Service
@Transactional
public class AcademyServiceImpl implements AcademyService {

	@Autowired
	private AcademyDao academyDao;

	@Autowired
	private AcademyHistoryDao academyHistoryDao;

	@Autowired
	private AcademySubjectDao academySubjectDao;
	
	@Autowired
	private AttachService attachService;
	
	@Autowired
	private AttachDao attachDao;

	// ==================== 학원정보 ====================

	@Transactional
	@Override
	public int insert(
			AcademyDto academyDto,
			List<MultipartFile> images
			) throws IllegalStateException, IOException {
		// 학원정보가 이미 존재하면 추가 등록 금지
		AcademyDto findAcademyDto = academyDao.selectOne();

		if(findAcademyDto != null) {
			throw new AlreadyExistsException("학원정보는 한 번만 등록할 수 있습니다.");
		}
		
		int academyNo = academyDao.sequence();

		academyDto.setAcademyNo(academyNo);

		academyDao.insert(academyDto);
		
		//학원 이미지 등록
		if (images != null && images.size() > 0) {
			for(MultipartFile image : images) {
				if(!image.isEmpty()) {
					//attach 테이블 + 실제 파일 저장
					int attachNo = attachService.save(image);
					
					//academy_file연결
					academyDao.connect(academyNo, attachNo);
				}
			}
		}
		return academyNo;
	}

	@Override
	public AcademyDetailResponseVO selectDetail() {

		// 학원정보는 항상 1개만 존재
		AcademyDto academy = academyDao.selectOne();
		
		if (academy == null) {
			throw new TargetNotfoundException();
	    }

		int academyNo = academy.getAcademyNo();

		// 해당 학원의 연혁 조회
		List<AcademyHistoryDto> historyList =
				academyHistoryDao.selectList(academyNo);

		// 해당 학원의 과목 조회
		List<AcademySubjectDto> subjectList =
				academySubjectDao.selectList(academyNo);
		
		//학원 이미지 번호 조회
		List<Integer> imagesNos =
				academyDao.selectDetailImages(academyNo);
		
		//이미지 정보 조회
		List<AttachDto> imageList =
				attachDao.selectList(imagesNos);

		return AcademyDetailResponseVO.builder()
				.academy(academy)
				.historyList(historyList)
				.subjectList(subjectList)
				.imageList(imageList)
			.build();
	}

	@Override
	public void update(
			AcademyDto academyDto,
			List<MultipartFile> images
			) throws IllegalStateException, IOException{

		AcademyDto academy = academyDao.selectOne();

		if (academy == null) {
		    throw new TargetNotfoundException();
		}

		academyDto.setAcademyNo(academy.getAcademyNo());

		academyDao.update(academyDto);
		
		//새로운 이미지 추가
		if(images != null && images.size()>0) {
			for(MultipartFile image : images) {
				if(!image.isEmpty()) {
					int attachNo = attachService.save(image);
					
					academyDao.connect(
						academy.getAcademyNo(), attachNo
					);
				}
			}
		}
	}

	@Override
	public boolean delete() {
		AcademyDto academy = academyDao.selectOne();

		if (academy == null) {
		    throw new TargetNotfoundException();
		}
		
		int academyNo = academy.getAcademyNo();
		
		//1. 학원 이미지 번호 미리 조회
		List<Integer> imageNos = academyDao.selectDetailImages(academyNo);
		
		//2. 학원삭제
		boolean result = academyDao.delete(academyNo);
		
		//3. 이미지 DB정보 + 실제파일 삭제
		for(Integer attachNo : imageNos) {
			attachService.delete(attachNo);
		}
		
		return result;
	}

	// ==================== 학원연혁 ====================

	@Override
	public void insertHistory(AcademyHistoryDto academyHistoryDto) {

		AcademyDto academy = academyDao.selectOne();

		int academyHistoryNo = academyHistoryDao.sequence();

		academyHistoryDto.setAcademyHistoryNo(academyHistoryNo);
		academyHistoryDto.setAcademyNo(academy.getAcademyNo());

		academyHistoryDao.insert(academyHistoryDto);
	}

	@Override
	public AcademyHistoryDto updateHistory(
		int academyHistoryNo,
		AcademyHistoryDto academyHistoryDto) {

		AcademyHistoryDto findAcademyHistoryDto = academyHistoryDao.selectOne(academyHistoryNo);

		if(findAcademyHistoryDto == null) {
			throw new TargetNotfoundException();
		}

		academyHistoryDto.setAcademyHistoryNo(academyHistoryNo);

		academyHistoryDao.update(academyHistoryDto);

		return academyHistoryDto;
	}

	@Override
	public boolean deleteHistory(int academyHistoryNo) {
		return academyHistoryDao.delete(academyHistoryNo);
	}

	// ==================== 학원과목 ====================

	@Override
	public void insertSubject(AcademySubjectDto academySubjectDto) {

		AcademyDto academy = academyDao.selectOne();

		int academySubjectNo = academySubjectDao.sequence();

		academySubjectDto.setAcademySubjectNo(academySubjectNo);
		academySubjectDto.setAcademyNo(academy.getAcademyNo());

		academySubjectDao.insert(academySubjectDto);
	}

	@Override
	public AcademySubjectDto updateSubject(
		int academySubjectNo,
		AcademySubjectDto academySubjectDto) {

		AcademySubjectDto findAcademySubjectDto =
				academySubjectDao.selectOne(academySubjectNo);

		if(findAcademySubjectDto == null) {
			throw new TargetNotfoundException();
		}

		academySubjectDto.setAcademySubjectNo(academySubjectNo);

		academySubjectDao.update(academySubjectDto);

		return academySubjectDto;
	}

	@Override
	public boolean deleteSubject(int academySubjectNo) {
		return academySubjectDao.delete(academySubjectNo);
	}

	@Override
	public void deleteImage(int academyNo, int attachNo) {
		//이 학원에 연결된 이미지인지 확인
		List<Integer> imageNos =
				academyDao.selectDetailImages(academyNo);
		if(!imageNos.contains(attachNo)) {
			throw new GetOutException();
		}
		
		//DB + 실제 파일 삭제
		attachService.delete(attachNo);
		
	}

}