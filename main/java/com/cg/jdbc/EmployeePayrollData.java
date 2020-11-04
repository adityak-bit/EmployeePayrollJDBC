package com.cg.jdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class EmployeePayrollData {

	int id;
	String name;
	double salary;
	LocalDate startDate;
	String gender;
	List<String> deptList;
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate,String gender) {
		this(id, name, salary, startDate);
		this.gender = gender;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate,String gender, List<String> deptList) {
		this(id, name, salary, startDate, gender);
		this.deptList = deptList;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, salary, startDate, gender);
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + ", startDate="
				+ startDate + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (deptList == null) {
			if (other.deptList != null)
				return false;
		} else if (!deptList.equals(other.deptList))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	
}
