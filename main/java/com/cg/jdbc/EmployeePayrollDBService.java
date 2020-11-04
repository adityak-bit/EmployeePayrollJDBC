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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

	private int connectionCounter = 0;
	private PreparedStatement employeePayrollDataStatement;
	private static EmployeePayrollDBService employeePayrollDBService;

	private EmployeePayrollDBService() {}
	
	public static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}
	
	private synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String username = "root";
		String password = "Aditya@987";
		Connection connection;
		System.out.println("Processing thread: "+Thread.currentThread().getName() +
				           "Connecting to database with id : "+connectionCounter);
		connection = DriverManager.getConnection(jdbcURL,username,password);
		System.out.println("Processing thread: "+Thread.currentThread().getName() +
				           "id: "+connectionCounter + "Connection is established!!!!: "+connection);
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
	
	public Map<String, Double> readAverageSalaryByGender() {
		String sql = "SELECT gender, avg(salary) AS avg_salary FROM employee_payroll group by gender ";
		Map<String, Double> avgSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				String gender = resultSet.getString("gender");
				double avg_salary = resultSet.getDouble("avg_salary");
				avgSalaryMap.put(gender, avg_salary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return avgSalaryMap;
	}
	
	public Map<String, Double> readSumSalaryByGender() {
		String sql = "SELECT gender, sum(salary) AS sum_salary FROM employee_payroll group by gender ";
		Map<String, Double> sumSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				String gender = resultSet.getString("gender");
				double sum_salary = resultSet.getDouble("sum_salary");
				sumSalaryMap.put(gender, sum_salary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sumSalaryMap;
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
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				String gender = resultSet.getString("gender");
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, start, gender));
			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}
	
	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary = %.2f where name = %s", salary, name);
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return 0;
	}
	
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		String sql = "update employee_payroll set salary = ? where name = ?";
		try(Connection connection = this.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
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
	

	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate start, String gender) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(" INSERT INTO employee_payroll (name, salary, start, gender) " +
				                   " VALUES('%s', '%s', '%s', '%s') " , name, salary, Date.valueOf(start), gender);
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start, gender);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate start, String gender) {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try{
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}try(Statement statement = connection.createStatement()){
			String sql = String.format(" INSERT INTO employee_payroll (name, salary, start, gender) " +
	                   " VALUES('%s', '%s', '%s', '%s') " , name, salary, Date.valueOf(start), gender);
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
		
		try(Statement statement = connection.createStatement()){
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double net_pay = salary - tax;
			String sql = String.format(" INSERT INTO payroll_details " +
			" (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES " +
					" (%s, %s, %s, %s, %s, %s) ", employeeId, salary, deductions, taxable_pay, tax , net_pay );
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			if(connection != null)
				try {
					connection.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return employeePayrollData;
	}

	public void deleteEmployeeData(String name) {
		String sql = String.format("DELETE FROM employee_payroll WHERE name = '%s'", name);
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
