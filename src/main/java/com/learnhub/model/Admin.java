package com.learnhub.model;

/**
 * Admin entity representing portal administrators.
 * Demonstrates role-based differentiation and object-oriented inheritance.
 */
public class Admin extends User {
    private String department;
    private String employeeId;

    public Admin() {
        super();
        setRole(Role.ROLE_ADMIN);
    }

    public Admin(String id, String fullName, String email, String password, String employeeId, String department) {
        super(id, fullName, email, password, Role.ROLE_ADMIN);
        this.employeeId = employeeId;
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
