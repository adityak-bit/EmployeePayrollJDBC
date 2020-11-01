package com.cg.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

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
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				int id =  result.getInt("id");
				String name = result.getString("name");
				double basic_pay = result.getDouble("basic_pay");
				LocalDate startDate = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, basic_pay, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	
}
