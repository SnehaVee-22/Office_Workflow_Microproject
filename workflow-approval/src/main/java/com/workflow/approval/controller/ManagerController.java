package com.workflow.approval.controller;

import com.workflow.approval.dto.request.DecisionRequest;
import com.workflow.approval.dto.response.DashboardStatsResponse;
import com.workflow.approval.dto.response.RequestResponse;
import com.workflow.approval.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController 
{

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) 
    {
        this.managerService = managerService;
    }

    
    @GetMapping("/requests/pending")
    public ResponseEntity<List<RequestResponse>> getPendingRequests(Authentication auth) 
    {
        return ResponseEntity.ok(managerService.getPendingRequests(auth.getName()));
    }


    @PatchMapping("/requests/{id}/approve")
    public ResponseEntity<RequestResponse> approveRequest(@PathVariable Long id,
                                                           @Valid @RequestBody DecisionRequest decision,
                                                           Authentication auth) 
    {
        return ResponseEntity.ok(managerService.approveRequest(id, auth.getName(), decision));
    }


    @PatchMapping("/requests/{id}/reject")
    public ResponseEntity<RequestResponse> rejectRequest(@PathVariable Long id,
                                                          @Valid @RequestBody DecisionRequest decision,
                                                          Authentication auth) 
    {
        return ResponseEntity.ok(managerService.rejectRequest(id, auth.getName(), decision));
    }


    @GetMapping("/requests/history")
    public ResponseEntity<List<RequestResponse>> getHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication auth) 
    {
        return ResponseEntity.ok(managerService.getApprovalHistory(auth.getName(), startDate, endDate));
    }

    

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(Authentication auth) 
    {
        return ResponseEntity.ok(managerService.getDashboardStats(auth.getName()));
    }
}
