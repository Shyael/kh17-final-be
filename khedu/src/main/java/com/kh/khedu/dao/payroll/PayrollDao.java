package com.kh.khedu.dao.payroll;

import java.util.List;

import com.kh.khedu.dto.payroll.PayrollDto;

public interface PayrollDao {

    long sequence();

    PayrollDto findByEmployeeAndPeriod(
            long employeeNo,
            int payrollYear,
            int payrollMonth
    );

    void add(PayrollDto payrollDto);

    PayrollDto find(long payrollNo);

    boolean updateCalculation(PayrollDto payrollDto);

    boolean changeStatus(PayrollDto payrollDto);

    List<PayrollDto> findAllByEmployee(long employeeNo);

}
