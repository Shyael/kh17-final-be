package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dao.AcademySubjectDao;
import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dao.TutorCareerDao;
import com.kh.khedu.dao.TutorDao;
import com.kh.khedu.dao.TutorSubjectDao;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.dto.TutorCareerDto;
import com.kh.khedu.dto.TutorDto;
import com.kh.khedu.dto.TutorSubjectDto;
import com.kh.khedu.vo.tutor.TutorDetailVO;
import com.kh.khedu.vo.tutor.TutorEmployeeVO;
import com.kh.khedu.vo.tutor.TutorListVO;

@Service
@Transactional
public class TutorServiceImpl implements TutorService {

	@Autowired
	private TutorDao tutorDao;

	@Autowired
	private TutorSubjectDao tutorSubjectDao;

	@Autowired
	private TutorCareerDao tutorCareerDao;

	@Autowired
	private AcademySubjectDao academySubjectDao;
	
	@Autowired
	private AttachService attachService;
	
	@Autowired
	private AttachDao attachDao;


	// ==================== 강사 기본정보 ====================

	@Override
	public TutorDto insert(
	        TutorDto tutorDto,
	        MultipartFile image
	) throws IllegalStateException, IOException {

	    // 1. 강사번호 생성
	    int tutorNo = tutorDao.sequence();
	    tutorDto.setTutorNo(tutorNo);
	    // 2. 강사정보 등록
	    tutorDao.insert(tutorDto);
	    
	    // 3. 이미지가 있으면 저장
	    if (image != null && !image.isEmpty()) {
	        // attach DB + 실제 파일 저장
	        int attachNo = attachService.save(image);
	        // tutor_file 연결
	        tutorDao.connect(tutorNo, attachNo);
	    }
	    return tutorDto;
	}

	@Override
	public List<TutorListVO> selectList() {
		return tutorDao.selectList();
	}

	@Override
	public List<TutorListVO> selectListBySubject(int academySubjectNo) {
		return tutorDao.selectListBySubject(academySubjectNo);
	}

	@Override
	public TutorDetailVO selectDetail(int tutorNo) {

	    TutorDetailVO tutorDetail = tutorDao.selectDetail(tutorNo);

	    if (tutorDetail == null) {
	        return null;
	    }

	    // 강사 담당과목 조회
	    List<TutorSubjectDto> subjectList =
	            tutorSubjectDao.selectList(tutorNo);

	    tutorDetail.setSubjectList(subjectList);

	    // 강사 학력/경력 조회
	    List<TutorCareerDto> careerList =
	            tutorCareerDao.selectList(tutorNo);

	    tutorDetail.setCareerList(careerList);
	    
	    //강사 이미지 번호 조회
	    Integer attachNo= tutorDao.selectImage(tutorNo);
	    
	    // 이미지가 있는 경우에만 파일정보 조회
	    if (attachNo != null ) {
	    	AttachDto image = attachDao.selectOne(attachNo);
	    	
	    	tutorDetail.setImage(image);
	    }

	    return tutorDetail;
	}

	@Override
	public TutorDto update(
	        int tutorNo,
	        TutorDto tutorDto,
	        MultipartFile image
	) throws IllegalStateException, IOException {

	    // 기존 강사 확인
	    TutorDto findTutorDto = tutorDao.selectOne(tutorNo);

	    if (findTutorDto == null) return null;
	    
	    // 기본정보 수정
	    tutorDto.setTutorNo(tutorNo);
	    tutorDao.update(tutorDto);

	    // 새로운 이미지가 들어온 경우만 이미지 교체
	    if (image != null && !image.isEmpty()) {
	        // 기존 이미지 번호 조회
	        Integer beforeAttachNo =
	                tutorDao.selectImage(tutorNo);
	        // 기존 이미지가 있으면 삭제
	        if (beforeAttachNo != null) {
	            attachService.delete(beforeAttachNo);
	        }
	        // 새 이미지 등록
	        int newAttachNo = attachService.save(image);
	        
	        // 강사와 새 이미지 연결
	        tutorDao.connect(
	                tutorNo,
	                newAttachNo
	        );
	    }
	    return tutorDto;
	}

	@Override
	public boolean delete(int tutorNo) {
		//강사 이미지 번호 미리 조회
		Integer  attachNo = tutorDao.selectImage(tutorNo);
		
		//강사 삭제
		boolean result = tutorDao.delete(tutorNo);
		
		//연결되어 있던 이미지 삭제
		if (attachNo != null) {
			attachService.delete(attachNo);
		}
		
		return result;
	}

	@Override
	public List<TutorEmployeeVO> selectAvailableEmployeeList() {
		return tutorDao.selectAvailableEmployeeList();
	}


	// ==================== 강사 과목 ====================

	@Override
	public void insertSubject(TutorSubjectDto tutorSubjectDto) {
		int tutorSubjectNo = tutorSubjectDao.sequence();

		tutorSubjectDto.setTutorSubjectNo(tutorSubjectNo);

		tutorSubjectDao.insert(tutorSubjectDto);
	}

	@Override
	public TutorSubjectDto updateSubject(
			int tutorSubjectNo,
			TutorSubjectDto tutorSubjectDto) {

		TutorSubjectDto findTutorSubjectDto =
				tutorSubjectDao.selectOne(tutorSubjectNo);

		if(findTutorSubjectDto == null) {
			return null;
		}

		tutorSubjectDto.setTutorSubjectNo(tutorSubjectNo);

		tutorSubjectDao.update(tutorSubjectDto);

		return tutorSubjectDto;
	}

	@Override
	public boolean deleteSubject(int tutorSubjectNo) {
		return tutorSubjectDao.delete(tutorSubjectNo);
	}


	// ==================== 강사 학력/경력 ====================

	@Override
	public void insertCareer(TutorCareerDto tutorCareerDto) {
		int tutorCareerNo = tutorCareerDao.sequence();

		tutorCareerDto.setTutorCareerNo(tutorCareerNo);

		tutorCareerDao.insert(tutorCareerDto);
	}

	@Override
	public TutorCareerDto updateCareer(
			int tutorCareerNo,
			TutorCareerDto tutorCareerDto) {

		TutorCareerDto findTutorCareerDto =
				tutorCareerDao.selectOne(tutorCareerNo);

		if(findTutorCareerDto == null) {
			return null;
		}

		tutorCareerDto.setTutorCareerNo(tutorCareerNo);

		tutorCareerDao.update(tutorCareerDto);

		return tutorCareerDto;
	}

	@Override
	public boolean deleteCareer(int tutorCareerNo) {
		return tutorCareerDao.delete(tutorCareerNo);
	}
	
	//이미지 삭제
	@Override
	public void deleteImage(int tutorNo) {
		//기존 이미지 번호 조회
		Integer attachNo = tutorDao.selectImage(tutorNo);
		
		//등록된 이미지가 없으면 종료
		if(attachNo == null) {
			return;
		}
		
		//attach DB + 실제파일 삭제
		attachService.delete(attachNo);
	}

}