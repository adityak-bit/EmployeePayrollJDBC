package com.cg.jdbc;

import java.time.LocalDate;
import java.util.HashMap;
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
	
	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if(result == 0) return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if(employeePayrollData != null) employeePayrollData.salary = salary;
	}
	
	private EmployeePayrollData getEmployeePayrollData(String name) {
		return employeePayrollList.stream()
				                  .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
				                  .findFirst()
				                  .orElse(null);
	}
	
	public void addEmployeesToPayroll(List<EmployeePayrollData> empList) {
		empList.forEach(employeePayrollData -> {
		//	System.out.println("Employee being added: "+employeePayrollData.name);
			this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
					                  employeePayrollData.startDate, employeePayrollData.gender);
		//	System.out.println("Employee added: "+employeePayrollData.name);
		});
		//System.out.println(this.employeePayrollList);
	}
	
	public void addEmployeesToPayrollUsingThreads(List<EmployeePayrollData> empList) {
		Map<Integer, Boolean> empAddnStatus = new HashMap<Integer, Boolean>();
		empList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				empAddnStatus.put(employeePayrollData.hashCode(), false);
				System.out.println("Employee being added: "+Thread.currentThread().getName());
				this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
		                                  employeePayrollData.startDate, employeePayrollData.gender);
				empAddnStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("Employee added: "+Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		while(empAddnStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(empList);
	}
	
	public void addEmployeeToPayroll(String name, double salary, LocalDate start, String gender) {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, start, gender));
	}
	
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name); 
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	

	public boolean checkEmployeePayrollAfterDeletion(String name) {
		for(EmployeePayrollData emp : employeePayrollList) {
			if(emp.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void deleteEmployeeFromPayroll(String name) {
		employeePayrollDBService.deleteEmployeeData(name);
	}

	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			return(employeePayrollList.size());
		return 0;
	}

	
}
