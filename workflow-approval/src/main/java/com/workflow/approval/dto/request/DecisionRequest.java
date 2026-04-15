package com.workflow.approval.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DecisionRequest 
{
    @NotBlank(message = "Remarks are required")
    private String remarks;

    public String getRemarks() 
    {
    	return remarks; 
    }
    public void setRemarks(String remarks) 
    { 
    	this.remarks = remarks; 
    }
}
