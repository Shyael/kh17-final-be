package com.kh.khedu.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.kh.khedu.dao.AssignmentDao;
import com.kh.khedu.dao.AssignmentSubmitDao;
import com.kh.khedu.dao.AttachDao;
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dto.AssignmentSubmitDto;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitDetailVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitListVO;
import com.kh.khedu.vo.assignment.AssignmentSubmitStudentListVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;

@Service
@Transactional
public class AssignmentSubmitServiceImpl implements AssignmentSubmitService {

    @Autowired
    private AssignmentSubmitDao assignmentSubmitDao;
    @Autowired
    private AssignmentDao assignmentDao;
    @Autowired
    private AttachService attachService;
    @Autowired
    private AttachDao attachDao;
    @Autowired
    private ParentStudentDao parentStudentDao;
    
    //공통 메소드
    // 과제 제출 기한 검사
    private void checkDueDate(int assignmentNo) {
    	
    	AssignmentDetailVO assignment = 
    			assignmentDao.selectOne(assignmentNo);
    	
    	if(assignment == null) {
    		throw new TargetNotfoundException("존재하지 않는 과제입니다");
    	}
    	
    	Timestamp dueDate = 
    			assignment.getAssignmentDueDate();
    	
    	//마감일이 없는 과제는 제한없음
    	if(dueDate == null) {
    		return;
    	}
    	
    	//현재 시간
    	Timestamp now =
                new Timestamp(System.currentTimeMillis());
    	
    	if (!dueDate.after(now)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "과제 제출 기한이 지났습니다."
            );
        }
    }
    
    //파일 공통 메소드
    private void setFiles(AssignmentSubmitDetailVO submit) {
    	if(submit == null) return;
    	
    	//제출 넘버를 통해서 파일번호들을 가져온다
    	List<Integer> fileNos = assignmentSubmitDao.selectFiles(submit.getSubmitNo());
    	
    	//파일번호를 통해서 파일상세 리스트를 가져온다
    	List<AttachDto> fileList = fileNos.isEmpty()
    										? List.of()
    										: attachDao.selectList(fileNos);
    	submit.setFileList(fileList);
    }
    
    // 본인 제출인지 확인
    private AssignmentSubmitDetailVO checkOwner(
            int submitNo,
            int studentNo) {

        AssignmentSubmitDetailVO submit =
                assignmentSubmitDao.selectOne(submitNo);

        if (submit == null) {
            throw new TargetNotfoundException();
        }

        if (submit.getStudentNo() != studentNo) {
            throw new GetOutException();
        }

        return submit;
    }
    
    //이 학생이 내 자녀인지 검사
    private void checkParentStudent(int parentNo, int studentNo) {
        List<ParentStudentVO> studentList = parentStudentDao.findByParentNo(parentNo);
        
        boolean connected =
                studentList.stream()
                        .anyMatch(student ->
                                student.getStudentNo() == studentNo
                        );
        if (!connected) {
            throw new GetOutException();
        }
    }
    
    // 과제 제출 등록
    @Override
    public int insert(
    		AssignmentSubmitDto assignmentSubmitDto,
    		List<MultipartFile> files
    		) throws IllegalStateException, IOException {
    	checkDueDate(assignmentSubmitDto.getAssignmentNo());
    	//시퀀스 번호 생성
        int submitNo = assignmentSubmitDao.sequence();
        
        assignmentSubmitDto.setSubmitNo(submitNo);
        assignmentSubmitDao.insert(assignmentSubmitDto);
        
        //제출 파일 등록
        if(files != null && files.size() > 0) {
        	for(MultipartFile file : files) {
        		if(!file.isEmpty()) {
        			//attach 테이블 + 실제 파일 저장
        			int attachNo = attachService.save(file);
        			assignmentSubmitDao.connect(submitNo, attachNo);
        		}
        	}
        }

        return submitNo;
    }

    // 특정 제출 상세 조회
    @Override
    public AssignmentSubmitDetailVO selectOne(int submitNo) {
    	
        AssignmentSubmitDetailVO submit = assignmentSubmitDao.selectOne(submitNo);
        
        if (submit == null) {
        	throw new TargetNotfoundException();
        }
        
        setFiles(submit);
        
        return submit;
    }

    // 특정 과제에 대한 특정 학생의 제출 조회
    @Override
    public AssignmentSubmitDetailVO selectOneByAssignmentStudent(
            AssignmentSubmitDto assignmentSubmitDto) {

    	 AssignmentSubmitDetailVO submit = assignmentSubmitDao.selectOneByAssignmentStudent(
                assignmentSubmitDto);
    	 
    	 setFiles(submit);
    	 
    	 return submit;
    }

    // 전체 과제 제출 목록 조회
    @Override
    public List<AssignmentSubmitListVO> selectList() {
        return assignmentSubmitDao.selectList();
    }

    // 특정 과제의 제출한 학생 목록 조회
    @Override
    public List<AssignmentSubmitListVO> selectListByAssignment(
            int assignmentNo) {

        return assignmentSubmitDao.selectListByAssignment(assignmentNo);
    }

    // 특정 과제의 전체 수강생 제출 현황 조회
    @Override
    public List<AssignmentSubmitStudentListVO> selectStudentListByAssignment(
            int assignmentNo) {

        return assignmentSubmitDao.selectStudentListByAssignment(
                assignmentNo);
    }

    // 제출 내용 수정
    @Override
    public boolean update(
    		AssignmentSubmitDto assignmentSubmitDto,
    		List<MultipartFile> files,
    		int studentNo
    		) throws IllegalStateException, IOException {
    	AssignmentSubmitDetailVO submit =
    	        checkOwner(
    	                assignmentSubmitDto.getSubmitNo(),
    	                studentNo
    	        );

        checkDueDate(
                submit.getAssignmentNo()
        );
    	boolean result = assignmentSubmitDao.update(assignmentSubmitDto);
    	
    	//신규 첨부파일 추가
        if(files != null && files.size() > 0) {
        	for(MultipartFile file : files) {
        		if(!file.isEmpty()) {
        			//attach 테이블 + 실제 파일 저장
        			int attachNo = attachService.save(file);
        			assignmentSubmitDao.connect(
    					assignmentSubmitDto.getSubmitNo(),
    					attachNo
        			);
        		}
        	}
        }
        return result;
    }

    // 강사 피드백 등록 및 수정
    @Override
    public boolean updateComment(AssignmentSubmitDto assignmentSubmitDto) {
        return assignmentSubmitDao.updateComment(assignmentSubmitDto);
    }

    // 과제 제출 삭제
    @Override
    public boolean delete(
            int submitNo,
            int studentNo) {

        // 본인 제출 확인
        AssignmentSubmitDetailVO submit =
                checkOwner(submitNo, studentNo);

        // 마감일 검사
        checkDueDate(submit.getAssignmentNo());

        // 파일번호 미리 조회
        List<Integer> fileNos =
                assignmentSubmitDao.selectFiles(submitNo);

        // 제출 삭제
        boolean result =
                assignmentSubmitDao.delete(submitNo);

        // 실제 파일 + attach 삭제
        for (Integer attachNo : fileNos) {
            attachService.delete(attachNo);
        }

        return result;
    }
    
    //
    @Override
    public void deleteFile(
            int submitNo,
            int attachNo,
            int studentNo) {

        // 본인 제출 확인
        AssignmentSubmitDetailVO submit =
                checkOwner(submitNo, studentNo);

        // 마감일 검사
        checkDueDate(submit.getAssignmentNo());

        // 해당 제출에 연결된 파일인지 확인
        List<Integer> fileNos =
                assignmentSubmitDao.selectFiles(submitNo);

        if (!fileNos.contains(attachNo)) {
            throw new GetOutException();
        }

        attachService.delete(attachNo);
    }

	@Override
	public AssignmentSubmitDetailVO selectOneByParentStudent(int parentNo, int studentNo, int assignmentNo) {
		//자기 자녀인지 확인
		checkParentStudent(parentNo, studentNo);
		
		AssignmentSubmitDto dto = 
				AssignmentSubmitDto.builder()
						.assignmentNo(assignmentNo)
						.studentNo(studentNo)
					.build();
		
		AssignmentSubmitDetailVO submit = 
				assignmentSubmitDao
					.selectOneByAssignmentStudent(dto);
		
		//제출한게 없으면 null가능
		setFiles(submit);
		
		return submit;
	}
}