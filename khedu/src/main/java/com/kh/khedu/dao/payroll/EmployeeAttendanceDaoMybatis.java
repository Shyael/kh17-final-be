package com.kh.khedu.dao.payroll;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.payroll.EmployeeAttendanceDto;
import com.kh.khedu.vo.employee.EmployeeDetailVO;
import com.kh.khedu.vo.payroll.response.AttendanceFindVO;
import com.kh.khedu.vo.payroll.response.AttendanceSearchResponseVO;
@Repository
public class EmployeeAttendanceDaoMybatis implements EmployeeAttendanceDao{
	@Autowired
    private SqlSession sqlSession;
	

    @Override
    public long sequence() {
        return sqlSession.selectOne(
                "mapper.attendance.attendanceSequence");
    }


    @Override
    public boolean add(EmployeeAttendanceDto dto) {
        return sqlSession.insert(
                "mapper.attendance.attendanceAdd",
                dto) > 0;
    }


    @Override
    public boolean update(EmployeeAttendanceDto dto) {
        return sqlSession.update(
                "mapper.attendance.attendanceUpdate",
                dto) > 0;
    }


    @Override
    public EmployeeAttendanceDto find(
            AttendanceFindVO findVO) {

        return sqlSession.selectOne(
                "mapper.attendance.attendanceFind",
                findVO);
    }


    @Override
    public List<AttendanceSearchResponseVO> search(
            long employeeNo,
           Timestamp startDate,
            Timestamp endDate) {

        Map<String, Object> params = new HashMap<>();

        params.put("employeeNo", employeeNo);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return sqlSession.selectList(
                "mapper.attendance.attendanceSearch",
                params);
    }


	@Override
	public EmployeeDetailVO findByAccountNo(int accountNo) {
		return sqlSession.selectOne("mapper.attendance.findByAccountNo",accountNo);
	}


	@Override
	public EmployeeAttendanceDto findBySchedule(long workScheduleNo) {
		return sqlSession.selectOne("mapper.attendance.findBySchedule",workScheduleNo);
	}


	

}
