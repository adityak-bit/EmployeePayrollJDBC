package com.cg.jdbc;

import java.util.List;

public class EmployeePayrollService {

	public enum IOService {DB_IO;}
	private List<EmployeePayrollData> employeePayrollList;
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		return employeePayrollList;
	}

}
