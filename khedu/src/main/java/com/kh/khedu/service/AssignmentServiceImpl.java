package com.kh.khedu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.khedu.dao.AssignmentDao;
import com.kh.khedu.dao.AssignmentSubmitDao;
import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentDao assignmentDao;
    
    @Autowired
    private AttachService attachService;
    
    @Autowired
    private AttachDao attachDao;
    
    @Autowired
    private AssignmentSubmitDao assignmentSubmitDao;
    
    // 과제 수정/삭제 권한 확인
    private AssignmentDetailVO checkAuthority(
            int assignmentNo,
            int employeeNo,
            boolean tutor) {

        AssignmentDetailVO assignment =
                assignmentDao.selectOne(assignmentNo);

        if (assignment == null) {
            throw new TargetNotfoundException();
        }

        // 강사는 본인이 작성한 과제만 가능
        if (tutor &&
                assignment.getEmployeeNo() != employeeNo) {
            throw new GetOutException();
        }

        // 원장/데스크는 tutor == false이므로 통과
        return assignment;
    }
    
    // 과제 등록
    @Override
    public int insert(
    		AssignmentDto assignmentDto,
    		List<MultipartFile> files
    		) throws IllegalStateException, IOException {
    	//시퀀스번호 생성
        int assignmentNo = assignmentDao.sequence();
        
        assignmentDto.setAssignmentNo(assignmentNo);

        assignmentDao.insert(assignmentDto);
        
        //과제 파일 등록
        if(files != null && files.size() > 0) {
        	for(MultipartFile file : files) {
        		if(!file.isEmpty()) {
        			//attach 테이블 + 실제 파일 저장
        			int attachNo = attachService.save(file);
        			//assignment_file연결
        			assignmentDao.connect(assignmentNo, attachNo);
        		}
        	}
        }
        return assignmentNo;
    }

    // 과제 상세 조회
    @Override
    public AssignmentDetailVO selectOne(int assignmentNo) {
    	//과제 상세 조회
    	AssignmentDetailVO assignment = assignmentDao.selectOne(assignmentNo);
    	
    	if(assignment == null) {
    		throw new TargetNotfoundException();
    	}
    	
    	// 과제에 연결된 파일 번호 조회
    	List<Integer> fileNos =
    	        assignmentDao.selectFiles(assignmentNo);

    	// 파일 상세정보 조회
    	List<AttachDto> fileList =
    	        fileNos.isEmpty()
    	                ? List.of()
    	                : attachDao.selectList(fileNos);

    	// 과제 상세정보에 파일 추가
    	assignment.setFileList(fileList);

    	return assignment;
    }

    // 전체 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectList() {
        return assignmentDao.selectList();
    }

    // 특정 강의의 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectListByCourse(int courseNo) {
        return assignmentDao.selectListByCourse(courseNo);
    }

    // 특정 강사가 등록한 과제 목록 조회
    @Override
    public List<AssignmentListVO> selectListByEmployee(int employeeNo) {
        return assignmentDao.selectListByEmployee(employeeNo);
    }

    // 학생이 수강 중인 강의의 과제 목록 조회
    @Override
    public List<StudentAssignmentListVO> selectListByStudent(int studentNo) {
        return assignmentDao.selectListByStudent(studentNo);
    }

    // 과제 수정
    @Override
    public boolean update(
    		AssignmentDto assignmentDto,
    		List<MultipartFile> files,
    		int employeeNo,
    		boolean tutor
    		) throws IllegalStateException, IOException {
    	
    	checkAuthority(
                assignmentDto.getAssignmentNo(),
                employeeNo,
                tutor
        );
    	
    	//1. 과제 기본정보 수정
        boolean result = assignmentDao.update(assignmentDto);
        
        //2. 신규 첨부파일 추가
        if(files != null && files.size() > 0) {
        	for(MultipartFile file : files) {
        		if(!file.isEmpty()) {
        			//attach 테이블 + 실제 파일 저장
        			int attachNo = attachService.save(file);
        			//assignment_file연결
        			assignmentDao.connect(
        					assignmentDto.getAssignmentNo(),
        					attachNo
        			);
        		}
        	}
        }
        return result;
    }

    // 과제 삭제
    @Override
    public boolean delete(int assignmentNo, int employeeNo, boolean tutor) {
    	// 존재 + 권한 확인
        checkAuthority(
                assignmentNo,
                employeeNo,
                tutor
        );
        
        // 1. 과제 자체 첨부파일 번호 미리 조회
        List<Integer> assignmentFileNos =
                assignmentDao.selectFiles(assignmentNo);

        // 2. 학생 제출 첨부파일 번호 미리 조회
        List<Integer> submitFileNos =
                assignmentSubmitDao.selectFilesByAssignment(assignmentNo);

        // 3. 과제 삭제
        // assignment_submit, assignment_submit_file은
        // ON DELETE CASCADE로 같이 삭제됨
        boolean result =
                assignmentDao.delete(assignmentNo);

        // 4. 과제 첨부파일 삭제
        for (Integer attachNo : assignmentFileNos) {
            attachService.delete(attachNo);
        }

        // 5. 학생 제출 첨부파일 삭제
        for (Integer attachNo : submitFileNos) {
            attachService.delete(attachNo);
        }

        return result;
    }
    
    //파일삭제
	@Override
	public void deleteFile(int assignmentNo, int attachNo, int employeeNo, boolean tutor) {
		// 존재 + 권한 확인
	    checkAuthority(
	            assignmentNo,
	            employeeNo,
	            tutor
	    );
		
		List<Integer> fileNos = 
				assignmentDao.selectFiles(assignmentNo);
		if(!fileNos.contains(attachNo)) {
			throw new GetOutException();
		}
		
		//DB + 실제 파일 삭제
		attachService.delete(attachNo);
	}

}