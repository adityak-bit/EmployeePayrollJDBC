package com.cg.jdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

	public enum IOService {DB_IO;}
	private List<EmployeePayrollData> employeePayrollList;
	private Map<String, Double> avgSalaryMap;
	private Map<String, Double> sumSalaryMap;
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
	
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService, LocalDate startDate, LocalDate endDate) {
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData(startDate, endDate);
		return employeePayrollList;
	}
	
	public Map<String, Double>  getAverageSalaryByGender(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			this.avgSalaryMap = employeePayrollDBService.readAverageSalaryByGender();
		return avgSalaryMap;
	}
	
	public Map<String, Double> getSumSalaryByGender(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			this.sumSalaryMap = employeePayrollDBService.readSumSalaryByGender();
		return sumSalaryMap;
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
	
	public void addEmployeeToPayroll(String name, double basic_pay, LocalDate start, String gender) {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, basic_pay, start, gender));
	}
	
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name); 
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	

	

	

}
