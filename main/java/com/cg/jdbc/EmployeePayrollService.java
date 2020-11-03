package com.cg.jdbc;

import java.util.List;

public class EmployeePayrollService {

	public enum IOService {DB_IO;}
	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;
	
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}
	
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList){
		this();
		this.employeePayrollList = employeePayrollList;
	}
	
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return employeePayrollList;
	}
	
	public void updateEmployeeSalary(String name, double basic_pay) {
		int result = employeePayrollDBService.updateEmployeeData(name, basic_pay);
		if(result == 0) return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if(employeePayrollData != null) employeePayrollData.basic_pay = basic_pay;
	}
	
	private EmployeePayrollData getEmployeePayrollData(String name) {
		return employeePayrollList.stream()
				                  .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
				                  .findFirst()
				                  .orElse(null);
	}
	
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name); 
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

}
