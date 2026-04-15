package com.workflow.approval.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateRequestDto 
{
    @NotBlank private String requestType;
    private String leaveType;
    private String duration;
    private String leavePlan;
    private String startDate;
    private String endDate;
    private String softwareName;
    private String softwareReason;
    @NotBlank private String description;

    public String getRequestType() 
    { 
    	return requestType; 
    }
    public void setRequestType(String requestType) 
    { 
    	this.requestType = requestType; 
    }
    public String getLeaveType() 
    { 
    	return leaveType; 
    }
    public void setLeaveType(String leaveType) 
    { 
    	this.leaveType = leaveType; 
    }
    public String getDuration() 
    { 
    	return duration; 
    }
    public void setDuration(String duration) 
    { 
    	this.duration = duration; 
    }
    public String getLeavePlan() 
    { 
    	return leavePlan; 
    }
    public void setLeavePlan(String leavePlan) 
    { 
    	this.leavePlan = leavePlan; 
    }
    public String getStartDate() 
    { 
    	return startDate; 
    }
    public void setStartDate(String startDate) 
    { 
    	this.startDate = startDate; 
    }
    public String getEndDate() 
    { 
    	return endDate; 
    }
    public void setEndDate(String endDate) 
    { 
    	this.endDate = endDate; 
    }
    public String getSoftwareName() 
    { 
    	return softwareName; 
    }
    public void setSoftwareName(String softwareName) 
    { 
    	this.softwareName = softwareName; 
    }
    public String getSoftwareReason() 
    { 
    	return softwareReason; 
    }
    public void setSoftwareReason(String softwareReason) 
    { 
    	this.softwareReason = softwareReason; 
    }
    public String getDescription() 
    { 
    	return description; 
    }
    public void setDescription(String description) 
    { 
    	this.description = description; 
    }
}
