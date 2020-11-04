package com.cg.jdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.cg.jdbc.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
	
	//UC2
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService  =  new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(9,employeePayrollData.size());
	}
	
	//UC3
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService  =  new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Bill", 3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Bill");
		Assert.assertTrue(result);
	}

	//UC5
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService service = new EmployeePayrollService();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollList = service.readEmployeePayrollData(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(9,employeePayrollList.size());
	}

	//UC6
	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService service = new EmployeePayrollService();
		Map<String, Double> avgSalaryMap = service.getAverageSalaryByGender(IOService.DB_IO); 
		Assert.assertEquals(3000000.00, avgSalaryMap.get("M"), 0.0);
	}
	
	//UC6
	@Test
	public void givenPayrollData_WhenSumSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService service = new EmployeePayrollService();
		Map<String, Double> sumSalaryMap = service.getSumSalaryByGender(IOService.DB_IO); 
		Assert.assertEquals(6000000.00, sumSalaryMap.get("M"), 0.0);
	}

	//UC7
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
		EmployeePayrollService service = new EmployeePayrollService();
		service.readEmployeePayrollData(IOService.DB_IO);
		service.addEmployeeToPayroll("Anonymous", 5000000.00, LocalDate.now(), "F");
		boolean result = service.checkEmployeePayrollInSyncWithDB("Anonymous");
		Assert.assertTrue(result);
	}
	
	@Test
    public void givenEmployee_WhenDeleted_ShouldSyncWithDB() {
    	EmployeePayrollService service = new EmployeePayrollService();
    	service.readEmployeePayrollData(IOService.DB_IO);
    	service.deleteEmployeeFromPayroll("Bill");
    	boolean result = service.checkEmployeePayrollAfterDeletion("Bill");
    	Assert.assertTrue(result);
    }
}
