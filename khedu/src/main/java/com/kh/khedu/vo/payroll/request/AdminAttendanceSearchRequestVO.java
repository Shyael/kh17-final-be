package com.kh.khedu.vo.payroll.request;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AdminAttendanceSearchRequestVO {
	private Timestamp startDate;
    private Timestamp endDate;
   private String accountName;
   private int employeeNo;
   private int accountNo;
}
