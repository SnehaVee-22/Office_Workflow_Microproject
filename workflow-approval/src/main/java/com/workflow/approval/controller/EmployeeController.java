package com.workflow.approval.controller;

import com.workflow.approval.dto.request.CreateRequestDto;
import com.workflow.approval.dto.response.NotificationResponse;
import com.workflow.approval.dto.response.RequestResponse;
import com.workflow.approval.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController 
{

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) 
    {
        this.employeeService = employeeService;
    }

    
    @GetMapping("/requests")
    public ResponseEntity<List<RequestResponse>> getMyRequests(Authentication auth) 
    {
        return ResponseEntity.ok(employeeService.getMyRequests(auth.getName()));
    }

    @PostMapping("/requests")
    public ResponseEntity<RequestResponse> createRequest(@Valid @RequestBody CreateRequestDto dto,
                                                          Authentication auth) 
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createRequest(auth.getName(), dto));
    }

    @PutMapping("/requests/{id}")
    public ResponseEntity<RequestResponse> updateRequest(@PathVariable Long id,
                                                          @RequestBody CreateRequestDto dto,
                                                          Authentication auth) 
    {
        return ResponseEntity.ok(employeeService.updateRequest(id, auth.getName(), dto));
    }

    @PatchMapping("/requests/{id}/cancel")
    public ResponseEntity<RequestResponse> cancelRequest(@PathVariable Long id,
                                                          Authentication auth) 
    {
        return ResponseEntity.ok(employeeService.cancelRequest(id, auth.getName()));
    }

    @GetMapping("/requests/search")
    public ResponseEntity<RequestResponse> searchRequest(@RequestParam String requestId,
                                                          Authentication auth) 
    {
        return ResponseEntity.ok(employeeService.searchByRequestId(requestId, auth.getName()));
    }

    

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication auth) 
    {
        return ResponseEntity.ok(employeeService.getNotifications(auth.getName()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationRead(@PathVariable Long id,
                                                      Authentication auth) 
    {
        employeeService.markNotificationRead(id, auth.getName());
        return ResponseEntity.ok().build();
    }
}
