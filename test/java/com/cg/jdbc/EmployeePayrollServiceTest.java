package com.cg.jdbc;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cg.jdbc.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService  =  new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(3,employeePayrollData.size());
	}
}
