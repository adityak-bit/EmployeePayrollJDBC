package com.cg.jdbc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
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
	
	//UC12
	@Test
    public void givenEmployee_WhenDeleted_ShouldSyncWithDB() {
    	EmployeePayrollService service = new EmployeePayrollService();
    	service.readEmployeePayrollData(IOService.DB_IO);
    	service.deleteEmployeeFromPayroll("Bill");
    	boolean result = service.checkEmployeePayrollAfterDeletion("Bill");
    	Assert.assertTrue(result);
    }
	
	//UC13/14/15
	@Test
	public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0, "A", 100.00, LocalDate.now(), "F"),
				new EmployeePayrollData(0, "B", 200.00, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "C", 300.00, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "D", 100.00, LocalDate.now(), "F"),
				new EmployeePayrollData(0, "E", 700.00, LocalDate.now(), "F"),
				new EmployeePayrollData(0, "F", 500.00, LocalDate.now(), "F"),
		};
		EmployeePayrollService service = new EmployeePayrollService();
		service.readEmployeePayrollData(IOService.DB_IO);
		Instant start =  Instant.now();
		service.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end =  Instant.now();
		System.out.println("Duration without thread: "+Duration.between(start, end));
		Instant startThread =  Instant.now();
		service.addEmployeesToPayrollUsingThreads(Arrays.asList(arrayOfEmps));
		Instant endThread =  Instant.now();
		System.out.println("Duration with thread: "+Duration.between(start, end));
		Assert.assertEquals(13, service.countEntries(IOService.DB_IO));
	}
}
