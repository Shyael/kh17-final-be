package com.kh.khedu.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.annotation.CommonsApiResponse;
import com.kh.khedu.annotation.CurrentUser;
import com.kh.khedu.dao.payroll.EmployeeAttendanceDao;
import com.kh.khedu.error.AdminChecker;
import com.kh.khedu.error.TargetNotfoundException;
import com.kh.khedu.service.attendance.AttendanceService;
import com.kh.khedu.service.attendance.EmployeeAttendanceService;
import com.kh.khedu.service.workschedule.EmployeeWorkScheduleService;
import com.kh.khedu.vo.attendance.AttendanceUpdateByAdminVO;
import com.kh.khedu.vo.attendance.SessionAttendanceDetailVO;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.jwt.TokenParseResponseVO;
import com.kh.khedu.vo.payroll.request.AdminAttendanceSearchRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceAbsentToNormalRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceLeaveRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToAbsentRequestVO;
import com.kh.khedu.vo.payroll.request.AttendanceNormalToNormalRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockInResponseVO;
import com.kh.khedu.vo.payroll.response.AttendanceClockOutResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleAddResponseVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleSearchResponseVO;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "직원 근태 관련 컨트롤러")
@CommonsApiResponse
@RestController
@RequestMapping("/api/employee/attendance")
public class EmployeeAttendanceRestController {

    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;

	@Autowired
	private EmployeeWorkScheduleService employeeWorkScheduleService;
	@Autowired
	private AdminChecker adminChecker;

	@Autowired
	private EmployeeAttendanceDao employeeAttendanceDao;
	
	// 학생출결
	@Autowired
	private AttendanceService attendanceService;



    @ApiResponse(
            responseCode = "200",
            description = "직원 출근 여부 조회 성공"
    )
    @GetMapping("/working")
    public boolean working(
            @CurrentUser TokenParseResponseVO parseVO) {

        return employeeAttendanceService.working(
                parseVO);
    }
    
 // 출근
    @ApiResponse(
            responseCode = "200",
            description = "직원 출근 성공"
    )
    @PostMapping("/clockIn")
    public AttendanceClockInResponseVO clockIn(
            
            @CurrentUser TokenParseResponseVO parseVO) {

        return employeeAttendanceService.clockIn(
               
                parseVO);
    }


    // 퇴근
    @ApiResponse(
            responseCode = "200",
            description = "직원 퇴근 성공"
    )
    @PatchMapping("/clockOut")
    public AttendanceClockOutResponseVO clockOut(
           @CurrentUser TokenParseResponseVO parseVO) {

        return employeeAttendanceService.clockOut(
                parseVO);
    }


    

//  여기서부터   관리자(혹은 데스크)
// 데스크 추가 될 경우가 있어서 어드민 전용 컨트롤러로 따로 빼지 않았습니다.
    
    
    
    
    
 // 근무 일정 등록
    @ApiResponse(
            responseCode = "200",
            description = "직원 근무 일정 등록 성공"
    )
    @PostMapping("/add")
    public WorkScheduleAddResponseVO add(
            @RequestBody WorkScheduleAddRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	adminChecker.AdminCheck(parseVO);
        return employeeWorkScheduleService.add(
                requestVO);
    }


    // 근무 일정 수정
    @ApiResponse(
            responseCode = "200",
            description = "직원 근무 일정 수정 성공"
    )
    @PatchMapping("/edit")
    public void update(
            @RequestBody WorkScheduleUpdateRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	adminChecker.AdminCheck(parseVO);
        employeeWorkScheduleService.update(
                requestVO);
    }

    //결근 등록
     @ApiResponse(
             responseCode = "200",
             description = "직원 결근 등록 성공"
     )
     @PostMapping("/absent")
     public void absent(
             @RequestBody AttendanceAbsentRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	 
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.absent(
                 requestVO,
                 parseVO);
     }

     
    //유급휴가 등록
     @ApiResponse(
             responseCode = "200",
             description = "직원 유급휴가 등록 성공"
     )
     @PostMapping("/paidLeave")
     public void paidLeave(
             @RequestBody AttendanceLeaveRequestVO requestVO,
             @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.paidLeave(
                 requestVO,
                 parseVO);
     }




     // 무급휴가 등록
     @ApiResponse(
             responseCode = "200",
             description = "직원 무급휴가 등록 성공"
     )
     @PostMapping("/unpaidLeave")
     public void unpaidLeave(
             @RequestBody AttendanceLeaveRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.unpaidLeave(
                 requestVO,
                 parseVO);
     }


     // 정상 -> 정상
     @ApiResponse(
             responseCode = "200",
             description = "정상 근태 수정 성공"
     )
     @PatchMapping("/normalToNormal")
     public void normalToNormal(
             @RequestBody AttendanceNormalToNormalRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.normalToNormal(
                 requestVO,
                 parseVO);
     }


     // 정상 -> 비근무
     @ApiResponse(
             responseCode = "200",
             description = "정상 근태를 비근무 상태로 변경 성공"
     )
     @PatchMapping("/normalToAbsent")
     public void normalToAbsent(
             @RequestBody AttendanceNormalToAbsentRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.normalToAbsent(
                 requestVO,
                 parseVO);
     }


     // 비근무 -> 정상
     @ApiResponse(
             responseCode = "200",
             description = "비근무 근태를 정상 상태로 변경 성공"
     )
     @PatchMapping("/absentToNormal")
     public void absentToNormal(
             @RequestBody AttendanceAbsentToNormalRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.absentToNormal(
                 requestVO,
                 parseVO);
     }


     // 비근무 -> 비근무
     @ApiResponse(
             responseCode = "200",
             description = "비근무 근태 상태 변경 성공"
     )
     @PatchMapping("/absentToAbsent")
     public void absentToAbsent(
             @RequestBody AttendanceAbsentToAbsentRequestVO requestVO,
            @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
         employeeAttendanceService.absentToAbsent(
                 requestVO,
                 parseVO);
     }

     //관리자용 조회
     @ApiResponse()
     @GetMapping(value ="/search", produces = MediaType.APPLICATION_JSON_VALUE)
     public WorkScheduleSearchResponseVO search(
    		AdminAttendanceSearchRequestVO request,
             @CurrentUser TokenParseResponseVO parseVO) {
    	 adminChecker.AdminCheck(parseVO);
    	 EmployeeDetailVO detailVO = employeeAttendanceDao.findByEmployeeNo(request.getEmployeeNo());
    	 if(detailVO==null) throw new TargetNotfoundException();
    	 return employeeWorkScheduleService.search(detailVO.getEmployeeNo()
    			 ,request.getStartDate()
    			 ,request.getEndDate());
     }
    
     
     /*
      * ========================================
      * 학생 출결쪽
      * ==============================================
      * */
     
     @ApiResponse(responseCode = "200", description = "수강생 출결 상태 수동 정정 (강사/관리자)")
     @PatchMapping("/{attendanceNo}")
     public ResponseEntity<String> updateAttendanceState(
             @PathVariable int attendanceNo,
             @RequestBody AttendanceUpdateByAdminVO request,
             @CurrentUser TokenParseResponseVO parseVO) {
         
         request.setAttendanceNo(attendanceNo);
         attendanceService.updateAttendanceByAdmin(request, parseVO);
         return ResponseEntity.ok("출결 상태가 [" + request.getAttendanceState() + "](으)로 변경되었습니다");
     }
     
     @GetMapping("/session/{sessionNo}")
     public ResponseEntity<SessionAttendanceDetailVO> getSessionAttendance(
             @PathVariable int sessionNo,
             @CurrentUser TokenParseResponseVO parseVO) {
         
         SessionAttendanceDetailVO response = attendanceService.getSessionAttendanceDetail(sessionNo, parseVO);
         return ResponseEntity.ok(response);
     }

}