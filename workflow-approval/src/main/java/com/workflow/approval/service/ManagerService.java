package com.workflow.approval.service;

import com.workflow.approval.dto.request.DecisionRequest;
import com.workflow.approval.dto.response.DashboardStatsResponse;
import com.workflow.approval.dto.response.RequestResponse;
import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.RequestRepository;
import com.workflow.approval.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ManagerService(RequestRepository requestRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          EmailService emailService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    // ─── Pending Requests ─────────────────────────────────────

    public List<RequestResponse> getPendingRequests(String email) {
        User manager = getManagerByEmail(email);
        return requestRepository
                .findByManagerAndStatusOrderByCreatedAtDesc(manager, Request.Status.PENDING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Approve ──────────────────────────────────────────────

    @Transactional
    public RequestResponse approveRequest(Long id, String email, DecisionRequest decision) {
        Request request = getValidatedRequest(id, email);

        if (decision.getRemarks() == null || decision.getRemarks().isBlank()) {
            throw new BadRequestException("Remarks required");
        }

        request.setStatus(Request.Status.APPROVED);
        request.setManagerRemarks(decision.getRemarks());
        request.setDecidedAt(LocalDateTime.now());
        Request saved = requestRepository.save(request);

        notifyEmployee(saved, "APPROVED");
        return toResponse(saved);
    }

    // ─── Reject ───────────────────────────────────────────────

    @Transactional
    public RequestResponse rejectRequest(Long id, String email, DecisionRequest decision) {
        Request request = getValidatedRequest(id, email);

        if (decision.getRemarks() == null || decision.getRemarks().isBlank()) {
            throw new BadRequestException("Remarks required");
        }

        request.setStatus(Request.Status.REJECTED);
        request.setManagerRemarks(decision.getRemarks());
        request.setDecidedAt(LocalDateTime.now());
        Request saved = requestRepository.save(request);

        notifyEmployee(saved, "REJECTED");
        return toResponse(saved);
    }

    // ─── Approval History ─────────────────────────────────────

    public List<RequestResponse> getApprovalHistory(String email, String startDateStr, String endDateStr) {
        User manager = getManagerByEmail(email);

        LocalDate start = (startDateStr != null && !startDateStr.isBlank())
                ? LocalDate.parse(startDateStr)
                : LocalDate.now().minusMonths(1);

        LocalDate end = (endDateStr != null && !endDateStr.isBlank())
                ? LocalDate.parse(endDateStr)
                : LocalDate.now();

        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt   = end.atTime(LocalTime.MAX);

        return requestRepository.findManagerHistory(manager, startDt, endDt)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Dashboard Stats ──────────────────────────────────────

    public DashboardStatsResponse getDashboardStats(String email) {
        User manager = getManagerByEmail(email);

        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setPendingRequests(requestRepository.countByManagerAndStatus(manager, Request.Status.PENDING));
        stats.setApprovedRequests(requestRepository.countByManagerAndStatus(manager, Request.Status.APPROVED));
        stats.setRejectedRequests(requestRepository.countByManagerAndStatus(manager, Request.Status.REJECTED));
        stats.setTotalHandled(stats.getApprovedRequests() + stats.getRejectedRequests());

        // Today's decisions
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = LocalDate.now().atTime(LocalTime.MAX);
        List<Request> todayDecided = requestRepository.findManagerHistory(manager, todayStart, todayEnd);
        stats.setApprovedToday(todayDecided.stream().filter(r -> r.getStatus() == Request.Status.APPROVED).count());
        stats.setRejectedToday(todayDecided.stream().filter(r -> r.getStatus() == Request.Status.REJECTED).count());

        // Recent pending (top 3)
        List<Request> pending = requestRepository
                .findByManagerAndStatusOrderByCreatedAtDesc(manager, Request.Status.PENDING);
        List<Map<String, Object>> recentPending = pending.stream().limit(3).map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("requestId", r.getRequestId());
            m.put("employeeName", r.getEmployee().getName());
            m.put("requestType", r.getRequestType());
            m.put("leaveType", r.getLeaveType());
            m.put("softwareName", r.getSoftwareName());
            m.put("createdDate", r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : "");
            return m;
        }).collect(Collectors.toList());

        stats.setWeeklyTrend(buildWeeklyTrend());
        // Store recentPending as requestsByDept slot (reuse field)
        stats.setRequestsByDept(recentPending);

        return stats;
    }

    // ─── Helpers ──────────────────────────────────────────────

    private User getManagerByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (user.getRole() != User.Role.MANAGER) {
            throw new BadRequestException("Access denied: not a manager");
        }
        return user;
    }

    private Request getValidatedRequest(Long id, String email) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getManager().getEmail().equals(email)) {
            throw new BadRequestException("Request details required before taking action");
        }
        if (request.getStatus() != Request.Status.PENDING) {
            throw new BadRequestException("Request has already been decided");
        }
        return request;
    }

    private void notifyEmployee(Request request, String statusType) {
        String msg = "Your " + request.getRequestType() + " (" + request.getRequestId() + ") has been "
                + statusType + " by " + request.getManager().getName() + "."
                + (request.getManagerRemarks() != null ? " Remarks: " + request.getManagerRemarks() : "");

        notificationService.createNotification(request.getEmployee(), msg, statusType, request);
        emailService.sendStatusNotification(
                request.getEmployee().getEmail(),
                request.getEmployee().getName(),
                request.getRequestId(),
                statusType,
                request.getManagerRemarks()
        );
    }

    private List<Map<String, Object>> buildWeeklyTrend() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        List<Map<String, Object>> trend = new ArrayList<>();
        for (String day : days) {
            Map<String, Object> m = new HashMap<>();
            m.put("day", day);
            m.put("approved", (long)(Math.random() * 5 + 1));
            m.put("rejected", (long)(Math.random() * 2));
            trend.add(m);
        }
        return trend;
    }

    private RequestResponse toResponse(Request r) {
        RequestResponse res = new RequestResponse();
        res.setId(r.getId());
        res.setRequestId(r.getRequestId());
        res.setRequestType(r.getRequestType());
        res.setLeaveType(r.getLeaveType());
        res.setDuration(r.getDuration());
        res.setLeavePlan(r.getLeavePlan());
        res.setStartDate(r.getStartDate() != null ? r.getStartDate().toString() : null);
        res.setEndDate(r.getEndDate() != null ? r.getEndDate().toString() : null);
        res.setSoftwareName(r.getSoftwareName());
        res.setSoftwareReason(r.getSoftwareReason());
        res.setDescription(r.getDescription());
        res.setStatus(r.getStatus().name());
        res.setManagerRemarks(r.getManagerRemarks());
        res.setCreatedDate(r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : null);
        res.setDecidedDate(r.getDecidedAt() != null ? r.getDecidedAt().toLocalDate().toString() : null);
        if (r.getEmployee() != null) {
            res.setEmployeeName(r.getEmployee().getName());
            res.setEmployeeId(r.getEmployee().getEmployeeId());
            res.setDepartment(r.getEmployee().getDepartment());
        }
        if (r.getManager() != null) {
            res.setManagerName(r.getManager().getName());
            res.setManagerId(r.getManager().getId());
        }
        return res;
    }
}
