package com.kh.khedu.dao.payroll;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.khedu.dto.payroll.EmployeeWorkScheduleDto;
import com.kh.khedu.vo.employee.EmployeeSearchByNameVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleAddRequestVO;
import com.kh.khedu.vo.payroll.request.WorkScheduleUpdateRequestVO;
import com.kh.khedu.vo.payroll.response.WorkScheduleResponseVO;
@Repository
public class EmployeeWorkScheduleDaoMybatis implements EmployeeWorkScheduleDao {

	@Autowired
	private SqlSession sqlSession;
	@Override
	public long sequence() {
		return sqlSession.selectOne(
                "mapper.workSchedule.workScheduleSequence"
        );
	}

	@Override
	public boolean add(WorkScheduleAddRequestVO request) {
		  return sqlSession.insert(
	                "mapper.workSchedule.workScheduleAdd",
	                request
	        ) > 0;
	}

	@Override
	public boolean update(WorkScheduleUpdateRequestVO request) {
		  return sqlSession.update(
	                "mapper.workSchedule.workScheduleUpdate",
	                request
	        ) > 0;
	}

	@Override
	public EmployeeWorkScheduleDto find(
	        EmployeeSearchByNameVO employeeVO,
	        Timestamp scheduledWorkDate) {

	    Map<String, Object> params =
	            new HashMap<>();

	    params.put(
	            "employeeNo",
	            employeeVO.getEmployeeNo()
	    );

	    params.put(
	            "scheduledWorkDate",
	            scheduledWorkDate
	    );

	    return sqlSession.selectOne(
	            "mapper.workSchedule.findTheWorkDay",
	            params
	    );
	}	

	@Override
	public List<WorkScheduleResponseVO> search(
	        long employeeNo,
	        Timestamp startDate,
	        Timestamp endDate) {

	    Map<String, Object> params =
	            new HashMap<>();

	    params.put(
	            "employeeNo",
	            employeeNo);

	    params.put(
	            "startDate",
	            startDate);

	    params.put(
	            "endDate",
	            endDate);


	    return sqlSession.selectList(
	            "mapper.workSchedule.workScheduleSearch",
	            params);
	}
	@Override
	public EmployeeWorkScheduleDto findByNo(
	        long workScheduleNo) {

	    return sqlSession.selectOne(
	            "mapper.workSchedule.findWorkScheduleByNo",
	            workScheduleNo);
	}
	
	

		@Override
		public List<EmployeeWorkScheduleDto> findAutoAbsentTarget(
		        Timestamp now) {

		    return sqlSession.selectList(
		            "mapper.workSchedule.findAutoAbsentTarget",
		            now);
		}

		@Override
		public EmployeeWorkScheduleDto isExist(int employeeNo, Timestamp scheduledWorkDate) {
			Map<String,Object> params = new HashMap<>();
			
			params.put("employeeNo", employeeNo);
			params.put("scheduledWorkDate", scheduledWorkDate);
			
			return sqlSession.selectOne("mapper.workSchedule.isExist",params);
		}

		@Override
		public EmployeeWorkScheduleDto findByContract(long contractNo, Timestamp scheduledWorkDate) {
			Map<String,Object> params = new HashMap<>();
			
			params.put("contractNo", contractNo);
			params.put("scheduledWorkDate", scheduledWorkDate);
			
			return sqlSession.selectOne("mapper.workSchedule.findByContract", params);
		}

}
