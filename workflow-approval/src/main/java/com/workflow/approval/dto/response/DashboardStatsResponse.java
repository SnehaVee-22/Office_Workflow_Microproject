package com.workflow.approval.dto.response;

import java.util.List;
import java.util.Map;

public class DashboardStatsResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long totalManagers;
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long cancelledRequests;
    private long approvedToday;
    private long rejectedToday;
    private long totalHandled;
    private List<Map<String, Object>> requestsByDept;
    private List<Map<String, Object>> monthlyTrend;
    private List<Map<String, Object>> weeklyTrend;

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }
    public long getActiveEmployees() { return activeEmployees; }
    public void setActiveEmployees(long activeEmployees) { this.activeEmployees = activeEmployees; }
    public long getTotalManagers() { return totalManagers; }
    public void setTotalManagers(long totalManagers) { this.totalManagers = totalManagers; }
    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }
    public long getApprovedRequests() { return approvedRequests; }
    public void setApprovedRequests(long approvedRequests) { this.approvedRequests = approvedRequests; }
    public long getRejectedRequests() { return rejectedRequests; }
    public void setRejectedRequests(long rejectedRequests) { this.rejectedRequests = rejectedRequests; }
    public long getCancelledRequests() { return cancelledRequests; }
    public void setCancelledRequests(long cancelledRequests) { this.cancelledRequests = cancelledRequests; }
    public long getApprovedToday() { return approvedToday; }
    public void setApprovedToday(long approvedToday) { this.approvedToday = approvedToday; }
    public long getRejectedToday() { return rejectedToday; }
    public void setRejectedToday(long rejectedToday) { this.rejectedToday = rejectedToday; }
    public long getTotalHandled() { return totalHandled; }
    public void setTotalHandled(long totalHandled) { this.totalHandled = totalHandled; }
    public List<Map<String, Object>> getRequestsByDept() { return requestsByDept; }
    public void setRequestsByDept(List<Map<String, Object>> requestsByDept) { this.requestsByDept = requestsByDept; }
    public List<Map<String, Object>> getMonthlyTrend() { return monthlyTrend; }
    public void setMonthlyTrend(List<Map<String, Object>> monthlyTrend) { this.monthlyTrend = monthlyTrend; }
    public List<Map<String, Object>> getWeeklyTrend() { return weeklyTrend; }
    public void setWeeklyTrend(List<Map<String, Object>> weeklyTrend) { this.weeklyTrend = weeklyTrend; }
}
