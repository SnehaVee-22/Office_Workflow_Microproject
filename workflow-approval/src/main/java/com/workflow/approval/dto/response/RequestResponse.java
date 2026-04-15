package com.workflow.approval.dto.response;

public class RequestResponse {
    private Long id;
    private String requestId;
    private String employeeName;
    private String employeeId;
    private String department;
    private String requestType;
    private String leaveType;
    private String duration;
    private String leavePlan;
    private String startDate;
    private String endDate;
    private String softwareName;
    private String softwareReason;
    private String description;
    private String status;
    private String managerName;
    private Long managerId;
    private String managerRemarks;
    private String createdDate;
    private String decidedDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getLeavePlan() { return leavePlan; }
    public void setLeavePlan(String leavePlan) { this.leavePlan = leavePlan; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getSoftwareName() { return softwareName; }
    public void setSoftwareName(String softwareName) { this.softwareName = softwareName; }
    public String getSoftwareReason() { return softwareReason; }
    public void setSoftwareReason(String softwareReason) { this.softwareReason = softwareReason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public String getManagerRemarks() { return managerRemarks; }
    public void setManagerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getDecidedDate() { return decidedDate; }
    public void setDecidedDate(String decidedDate) { this.decidedDate = decidedDate; }
}
