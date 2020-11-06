package com.cg.jdbc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cg.jdbc.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollServiceTest {
	
	//UC2
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService service  =  new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = service.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(1,employeePayrollData.size());
	}
	
	//UC3
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
		EmployeePayrollService service  =  new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = service.readEmployeePayrollData(IOService.DB_IO);
		service.updateEmployeeSalary("Charlie", 3.00);
		boolean result = service.checkEmployeePayrollInSyncWithDB("Charlie");
		Assert.assertTrue(result);
	}

	//UC5
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService service = new EmployeePayrollService();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollList = service.readEmployeePayrollData(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(1,employeePayrollList.size());
	}

	//UC6
	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService service = new EmployeePayrollService();
		Map<String, Double> avgSalaryMap = service.getAverageSalaryByGender(IOService.DB_IO); 
		Assert.assertEquals(3.00, avgSalaryMap.get("M"), 0.0);
	}
	
	//UC6
	@Test
	public void givenPayrollData_WhenSumSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService service = new EmployeePayrollService();
		Map<String, Double> sumSalaryMap = service.getSumSalaryByGender(IOService.DB_IO); 
		Assert.assertEquals(3.00, sumSalaryMap.get("M"), 0.0);
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
	
	@Before
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	
	public EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employees");
		System.out.println("EMPLOYEE PAYROLL ENTRIES IN JSONServer:\n" +response.asString());
		EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(),
				                                                EmployeePayrollData[].class);
		return arrayOfEmps;
	}
	
	private Response addEmpToJSONServer(EmployeePayrollData data) {
		String empJson = new Gson().toJson(data);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}
	
	//REST-UC1
	@Test
	public void givenEmployeeDataInJSONServer_WhenRetrieved_ShouldMatchCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService service;
		service = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		long entries = service.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}
	
	//REST-UC2
	@Test
	public void givenNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService service;
		service = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		EmployeePayrollData data = new EmployeePayrollData(3, "A", 1.00,
				                                           LocalDate.of(2020, 01, 01));
		Response response = addEmpToJSONServer(data);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		
		data = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		service.addEmployeeToPayroll(data, IOService.REST_IO);
		long entries = service.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}

	

	
}
