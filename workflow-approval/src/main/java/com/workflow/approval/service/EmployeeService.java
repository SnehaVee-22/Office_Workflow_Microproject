package com.workflow.approval.service;

import com.workflow.approval.dto.request.CreateRequestDto;
import com.workflow.approval.dto.response.NotificationResponse;
import com.workflow.approval.dto.response.RequestResponse;
import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.RequestRepository;
import com.workflow.approval.repository.UserRepository;
import com.workflow.approval.util.RequestIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RequestIdGenerator requestIdGenerator;

    public EmployeeService(RequestRepository requestRepository,
                           UserRepository userRepository,
                           NotificationService notificationService,
                           RequestIdGenerator requestIdGenerator) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.requestIdGenerator = requestIdGenerator;
    }

    // ─── Get all requests for logged-in employee ──────────────

    public List<RequestResponse> getMyRequests(String email) {
        User employee = getUserByEmail(email);
        return requestRepository.findByEmployeeOrderByCreatedAtDesc(employee)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Create a new request ─────────────────────────────────

    @Transactional
    public RequestResponse createRequest(String email, CreateRequestDto dto) {
        User employee = getUserByEmail(email);

        if (employee.getManager() == null) {
            throw new BadRequestException("No manager assigned to your account. Contact admin.");
        }

        validateRequestDto(dto);

        Request request = new Request();
        request.setRequestId(requestIdGenerator.generate());
        request.setEmployee(employee);
        request.setManager(employee.getManager());
        request.setRequestType(dto.getRequestType());
        request.setDescription(dto.getDescription());
        request.setStatus(Request.Status.PENDING);

        if ("Leave Request".equalsIgnoreCase(dto.getRequestType())) {
            request.setLeaveType(dto.getLeaveType());
            request.setDuration(dto.getDuration());
            request.setLeavePlan(dto.getLeavePlan());
            if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {
                request.setStartDate(LocalDate.parse(dto.getStartDate()));
            }
            if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
                request.setEndDate(LocalDate.parse(dto.getEndDate()));
            }
        } else if ("Software Requirement".equalsIgnoreCase(dto.getRequestType())) {
            request.setSoftwareName(dto.getSoftwareName());
            request.setSoftwareReason(dto.getSoftwareReason());
        }

        Request saved = requestRepository.save(request);

        // Notify employee that request was submitted
        notificationService.createNotification(
            employee,
            "Your " + saved.getRequestType() + " (" + saved.getRequestId() + ") has been submitted and is awaiting manager approval.",
            "PENDING",
            saved
        );

        return toResponse(saved);
    }

    // ─── Edit a pending request ───────────────────────────────

    @Transactional
    public RequestResponse updateRequest(Long id, String email, CreateRequestDto dto) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getEmployee().getEmail().equals(email)) {
            throw new BadRequestException("You can only edit your own requests");
        }
        if (request.getStatus() != Request.Status.PENDING) {
            throw new BadRequestException("Edit Not Allowed: Only pending requests can be edited");
        }

        request.setDescription(dto.getDescription());

        if ("Leave Request".equalsIgnoreCase(request.getRequestType())) {
            if (dto.getLeaveType() != null)  request.setLeaveType(dto.getLeaveType());
            if (dto.getDuration() != null)   request.setDuration(dto.getDuration());
            if (dto.getLeavePlan() != null)  request.setLeavePlan(dto.getLeavePlan());
            if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {
                request.setStartDate(LocalDate.parse(dto.getStartDate()));
            }
            if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
                request.setEndDate(LocalDate.parse(dto.getEndDate()));
            }
        } else if ("Software Requirement".equalsIgnoreCase(request.getRequestType())) {
            if (dto.getSoftwareName() != null) request.setSoftwareName(dto.getSoftwareName());
            if (dto.getSoftwareReason() != null) request.setSoftwareReason(dto.getSoftwareReason());
        }

        return toResponse(requestRepository.save(request));
    }

    // ─── Cancel a pending request ─────────────────────────────

    @Transactional
    public RequestResponse cancelRequest(Long id, String email) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getEmployee().getEmail().equals(email)) {
            throw new BadRequestException("You can only cancel your own requests");
        }
        if (request.getStatus() != Request.Status.PENDING) {
            throw new BadRequestException("Action not allowed: Only pending requests can be cancelled");
        }

        request.setStatus(Request.Status.CANCELLED);
        Request saved = requestRepository.save(request);

        notificationService.createNotification(
            request.getEmployee(),
            "Your request " + saved.getRequestId() + " has been cancelled.",
            "CANCELLED",
            saved
        );

        return toResponse(saved);
    }

    // ─── Search by Request ID ─────────────────────────────────

    public RequestResponse searchByRequestId(String requestId, String email) {
        Request request = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getEmployee().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Request not found");
        }
        return toResponse(request);
    }

    // ─── Notifications ────────────────────────────────────────

    public List<NotificationResponse> getNotifications(String email) {
        return notificationService.getNotificationsForUser(email);
    }

    public void markNotificationRead(Long id, String email) {
        notificationService.markAsRead(id, email);
    }

    // ─── Helpers ──────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateRequestDto(CreateRequestDto dto) {
        if (dto.getRequestType() == null || dto.getRequestType().isBlank()) {
            throw new BadRequestException("Request type is required");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new BadRequestException("Description is required");
        }
        if ("Leave Request".equalsIgnoreCase(dto.getRequestType())) {
            if (dto.getLeaveType() == null || dto.getLeaveType().isBlank())
                throw new BadRequestException("Leave type is required");
            if (dto.getDuration() == null || dto.getDuration().isBlank())
                throw new BadRequestException("Duration is required");
            if (dto.getLeavePlan() == null || dto.getLeavePlan().isBlank())
                throw new BadRequestException("Leave plan is required");
            if (dto.getStartDate() == null || dto.getStartDate().isBlank())
                throw new BadRequestException("Start date is required");
        }
        if ("Software Requirement".equalsIgnoreCase(dto.getRequestType())) {
            if (dto.getSoftwareName() == null || dto.getSoftwareName().isBlank())
                throw new BadRequestException("Software name is required");
            if (dto.getSoftwareReason() == null || dto.getSoftwareReason().isBlank())
                throw new BadRequestException("Software reason is required");
        }
    }

    RequestResponse toResponse(Request r) {
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
