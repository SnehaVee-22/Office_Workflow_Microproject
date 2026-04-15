package com.workflow.approval.controller;

import com.workflow.approval.dto.request.CreateEmployeeRequest;
import com.workflow.approval.dto.request.MasterDataRequest;
import com.workflow.approval.dto.response.DashboardStatsResponse;
import com.workflow.approval.dto.response.UserResponse;
import com.workflow.approval.entity.Department;
import com.workflow.approval.entity.RequestType;
import com.workflow.approval.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController 
{

    private final AdminService adminService;

    public AdminController(AdminService adminService) 
    {
        this.adminService = adminService;
    }

    

    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getAllEmployees() 
    {
        return ResponseEntity.ok(adminService.getAllEmployees());
    }

    @PostMapping("/employees")
    public ResponseEntity<UserResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest req) 
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createEmployee(req));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<UserResponse> updateEmployee(@PathVariable Long id,
                                                       @Valid @RequestBody CreateEmployeeRequest req) 
    {
        return ResponseEntity.ok(adminService.updateEmployee(id, req));
    }

    @PatchMapping("/employees/{id}/toggle-active")
    public ResponseEntity<UserResponse> toggleActive(@PathVariable Long id) 
    {
        return ResponseEntity.ok(adminService.toggleActive(id));
    }

    

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() 
    {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody MasterDataRequest req) 
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createDepartment(req));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) 
    {
        adminService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    

    @GetMapping("/request-types")
    public ResponseEntity<List<RequestType>> getRequestTypes() 
    {
        return ResponseEntity.ok(adminService.getAllRequestTypes());
    }

    @PostMapping("/request-types")
    public ResponseEntity<RequestType> createRequestType(@Valid @RequestBody MasterDataRequest req) 
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createRequestType(req));
    }

    @DeleteMapping("/request-types/{id}")
    public ResponseEntity<Void> deleteRequestType(@PathVariable Long id) 
    {
        adminService.deleteRequestType(id);
        return ResponseEntity.noContent().build();
    }

    

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() 
    {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}
