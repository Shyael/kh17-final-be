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
import com.kh.khedu.dao.ParentStudentDao;
import com.kh.khedu.dto.AssignmentDto;
import com.kh.khedu.dto.AttachDto;
import com.kh.khedu.error.GetOutException;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.util.PageResponseVO;
import com.kh.khedu.vo.assignment.AssignmentDetailVO;
import com.kh.khedu.vo.assignment.AssignmentListVO;
import com.kh.khedu.vo.assignment.AssignmentSearchVO;
import com.kh.khedu.vo.assignment.AssignmentStudentSearchVO;
import com.kh.khedu.vo.assignment.StudentAssignmentListVO;
import com.kh.khedu.vo.parentStudent.ParentStudentVO;

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
    
    @Autowired
    private ParentStudentDao parentStudentDao;
    
    //공통 메소드
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
    //학부모-자녀 관계 확인
    private void checkParentStudent(
    		int parentNo,
    		int studentNo) {
    	List<ParentStudentVO> studentList = 
    			parentStudentDao.findByParentNo(parentNo);
    	
    	boolean connected = 
    			studentList.stream()
    				.anyMatch(student ->
    						student.getStudentNo() == studentNo
    				);
    	if(!connected) {
    		throw new GetOutException();
    	}
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

    // 특정 강의의 최근 5개 과제 목록 조회
    @Override
	public List<AssignmentListVO> selectRecentListByCourse(int courseNo) {
    	return assignmentDao.selectRecentListByCourse(courseNo);
	}

    // 학생이 수강 중인 강의의 과제 목록 조회
    @Override
    public List<StudentAssignmentListVO> selectListByStudent(int studentNo) {
        return assignmentDao.selectListByStudent(studentNo);
    }
    
    //강사 페이지네이션+ 검색 + 과제 목록
    @Override
	public PageResponseVO<AssignmentListVO> selectManageList(AssignmentSearchVO search, int employeeNo, boolean tutor) {
		//강사는 본인 과제만
    	if (tutor){
    		search.setEmployeeNo(employeeNo);
    	}
    	//원장/관리자는 전체 
    	else {
    		search.setEmployeeNo(null);
    	}
    	
    	List<AssignmentListVO> assignmentList = assignmentDao.selectManageSearchList(search);
    	
    	int totalCount = assignmentDao.selectManageCount(search);
    	
    	return new PageResponseVO<>(
    			assignmentList,
    			totalCount,
    			search
    	);
	}
    
    //학생/학부모 페이지네이션 + 검색 + 과제목록
    @Override
	public PageResponseVO<StudentAssignmentListVO> selectStudentList(AssignmentStudentSearchVO search, int studentNo) {
		//로그인한 학생 번호 세팅
    	search.setStudentNo(studentNo);
    	
    	//현재 페이지 과제 목록
    	List<StudentAssignmentListVO> assignmentList =
    			assignmentDao.selectStudentSearchList(search);
    	
    	//검색조건에 해당하는 전체 개수
    	int totalCount = assignmentDao.selectStudentCount(search);
    	
    	return new PageResponseVO<>(
    			assignmentList,
    			totalCount,
    			search
    	);
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
	
	//학부모용 : 자녀 과제 목록 조회
	@Override
	public PageResponseVO<StudentAssignmentListVO> selectListByParentStudent(
	        AssignmentStudentSearchVO search,
	        int parentNo,
	        int studentNo) {
	    //학부모-자녀 관계 검증
	    checkParentStudent(parentNo, studentNo);

	    //조회할 학생번호 세팅
	    search.setStudentNo(studentNo);

	    List<StudentAssignmentListVO> list = assignmentDao.selectStudentSearchList(search);

	    int totalCount = assignmentDao.selectStudentCount(search);

	    return new PageResponseVO<>(
	            list,
	            totalCount,
	            search
	    );
	}
	
	// 학부모용 : 자녀 과제 상세 조회
	@Override
	public AssignmentDetailVO selectOneByParentStudent(int parentNo, int studentNo, int assignmentNo) {
		 // 1. 자신의 자녀인지 확인
	    checkParentStudent(parentNo, studentNo);
	    // 2. 해당 자녀가 볼 수 있는 과제인지 확인
	    List<StudentAssignmentListVO> assignmentList =
	            assignmentDao.selectListByStudent(studentNo);
	    boolean accessible =
	            assignmentList.stream()
	                    .anyMatch(assignment ->
	                            assignment.getAssignmentNo() == assignmentNo
	                    );

	    if (!accessible) {
	        throw new GetOutException();
	    }
	    // 3. 기존 과제 상세조회 재사용
	    return selectOne(assignmentNo);
	}

}