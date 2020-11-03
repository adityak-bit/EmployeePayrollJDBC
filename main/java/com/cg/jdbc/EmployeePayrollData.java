package com.cg.jdbc;

import java.time.LocalDate;

public class EmployeePayrollData {

	int id;
	String name;
	double basic_pay;
	LocalDate startDate;
	String gender;
	public EmployeePayrollData(int id, String name, double basic_pay, LocalDate startDate) {
		super();
		this.id = id;
		this.name = name;
		this.basic_pay = basic_pay;
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String name, double basic_pay, LocalDate startDate,String gender) {
		this(id, name, basic_pay, startDate);
		this.gender = gender;
	}
	

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", basic_pay=" + basic_pay + ", startDate="
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
		if (Double.doubleToLongBits(basic_pay) != Double.doubleToLongBits(other.basic_pay))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
}
