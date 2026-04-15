package com.workflow.approval.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateEmployeeRequest 
{
    @NotBlank private String employeeId;
    @NotBlank private String name;
    @NotBlank @Email private String email;
    @NotBlank private String department;
    @NotBlank private String role;
    private Long managerId;
    private String password;

    public String getEmployeeId() 
    { 
    	return employeeId; 
    }
    public void setEmployeeId(String employeeId) 
    { 
    	this.employeeId = employeeId; 
    }
    public String getName() 
    { 
    	return name; 
    }
    public void setName(String name) 
    { 
    	this.name = name; 
    }
    public String getEmail() 
    { 
    	return email; 
    }
    public void setEmail(String email) 
    { 
    	this.email = email; 
    }
    public String getDepartment() 
    { 
    	return department; 
    }
    public void setDepartment(String department) 
    { 
    	this.department = department; 
    }
    public String getRole() 
    { 
    	return role; 
    }
    public void setRole(String role) 
    { 
    	this.role = role; 
    }
    public Long getManagerId() 
    { 
    	return managerId; 
    }
    public void setManagerId(Long managerId) 
    { 
    	this.managerId = managerId; 
    }
    public String getPassword() 
    { 
    	return password; 
    }
    public void setPassword(String password) 
    { 
    	this.password = password; 
    }
}
