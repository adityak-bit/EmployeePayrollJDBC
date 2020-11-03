package com.cg.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	private PreparedStatement employeePayrollDataStatement;
	private static EmployeePayrollDBService employeePayrollDBService;

	private EmployeePayrollDBService() {}
	
	public static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}
	
	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String username = "root";
		String password = "Aditya@987";
		Connection connection;
		System.out.println("Connecting to database: "+jdbcURL);
		connection = DriverManager.getConnection(jdbcURL,username,password);
		System.out.println("Connection is successfull!!!!: "+connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql = "SELECT * FROM employee_payroll;";
		return this.getEmployeeDataUsingDB(sql);
	}
	
	public List<EmployeePayrollData> readData(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("SELECT * FROM employee_payroll where start between '%s' and '%s'", Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeeDataUsingDB(sql);
	}

	List<EmployeePayrollData> getEmployeeDataUsingDB(String sql){
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	
	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeePayrollList =  null;
		if(this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double basic_pay = resultSet.getDouble("basic_pay");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				String gender = resultSet.getString("gender");
				employeePayrollList.add(new EmployeePayrollData(id, name, basic_pay, start, gender));
			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return employeePayrollList;
	}

	public int updateEmployeeData(String name, double basic_pay) {
		return this.updateEmployeeDataUsingPreparedStatement(name, basic_pay);
	}
	
	private int updateEmployeeDataUsingStatement(String name, double basic_pay) {
		String sql = String.format("update employee_payroll set basic_pay = %.2f where name = %s", basic_pay, name);
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return 0;
	}
	
	private int updateEmployeeDataUsingPreparedStatement(String name, double basic_pay) {
		String sql = "update employee_payroll set basic_pay = ? where name = ?";
		try(Connection connection = this.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, basic_pay);
			preparedStatement.setString(2, name);
			return preparedStatement.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	
}
