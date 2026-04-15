package com.workflow.approval.service;

import com.workflow.approval.dto.request.CreateEmployeeRequest;
import com.workflow.approval.dto.request.MasterDataRequest;
import com.workflow.approval.dto.response.DashboardStatsResponse;
import com.workflow.approval.dto.response.UserResponse;
import com.workflow.approval.entity.Department;
import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.RequestType;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.DepartmentRepository;
import com.workflow.approval.repository.RequestRepository;
import com.workflow.approval.repository.RequestTypeRepository;
import com.workflow.approval.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AdminService(UserRepository userRepository,
                        DepartmentRepository departmentRepository,
                        RequestTypeRepository requestTypeRepository,
                        RequestRepository requestRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ─── Employees ────────────────────────────────────────────

    public List<UserResponse> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != User.Role.ADMIN)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createEmployee(CreateEmployeeRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByEmployeeId(req.getEmployeeId())) {
            throw new BadRequestException("Employee ID already exists");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User user = new User();
        user.setEmployeeId(req.getEmployeeId());
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setDepartment(req.getDepartment());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.valueOf(req.getRole().toUpperCase()));
        user.setActive(true);

        if (user.getRole() == User.Role.EMPLOYEE) {
            if (req.getManagerId() == null) {
                throw new BadRequestException("Manager is required for employee role");
            }
            User manager = userRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            user.setManager(manager);
        }

        User saved = userRepository.save(user);
        emailService.sendCredentials(saved.getEmail(), saved.getName(), req.getPassword());
        return toUserResponse(saved);
    }

    @Transactional
    public UserResponse updateEmployee(Long id, CreateEmployeeRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!user.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already in use by another account");
        }

        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setDepartment(req.getDepartment());
        user.setRole(User.Role.valueOf(req.getRole().toUpperCase()));

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            emailService.sendCredentials(user.getEmail(), user.getName(), req.getPassword());
        }

        if (user.getRole() == User.Role.EMPLOYEE && req.getManagerId() != null) {
            User manager = userRepository.findById(req.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            user.setManager(manager);
        } else if (user.getRole() == User.Role.MANAGER) {
            user.setManager(null);
        }

        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        user.setActive(!user.isActive());
        return toUserResponse(userRepository.save(user));
    }

    // ─── Departments ──────────────────────────────────────────

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department createDepartment(MasterDataRequest req) {
        if (departmentRepository.existsByName(req.getName())) {
            throw new BadRequestException("Department already exists: " + req.getName());
        }
        Department dept = new Department();
        dept.setName(req.getName());
        dept.setDescription(req.getDescription());
        return departmentRepository.save(dept);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found");
        }
        departmentRepository.deleteById(id);
    }

    // ─── Request Types ────────────────────────────────────────

    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }

    @Transactional
    public RequestType createRequestType(MasterDataRequest req) {
        if (requestTypeRepository.existsByName(req.getName())) {
            throw new BadRequestException("Request type already exists: " + req.getName());
        }
        RequestType rt = new RequestType();
        rt.setName(req.getName());
        rt.setDescription(req.getDescription());
        return requestTypeRepository.save(rt);
    }

    @Transactional
    public void deleteRequestType(Long id) {
        if (!requestTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Request type not found");
        }
        requestTypeRepository.deleteById(id);
    }

    // ─── Dashboard Stats ──────────────────────────────────────

    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        List<User> allUsers = userRepository.findAll();
        List<User> employees = allUsers.stream().filter(u -> u.getRole() == User.Role.EMPLOYEE).collect(Collectors.toList());
        List<User> managers  = allUsers.stream().filter(u -> u.getRole() == User.Role.MANAGER).collect(Collectors.toList());

        stats.setTotalEmployees(employees.size());
        stats.setActiveEmployees(employees.stream().filter(User::isActive).count());
        stats.setTotalManagers(managers.size());
        stats.setTotalRequests(requestRepository.count());
        stats.setPendingRequests(requestRepository.countByStatus(Request.Status.PENDING));
        stats.setApprovedRequests(requestRepository.countByStatus(Request.Status.APPROVED));
        stats.setRejectedRequests(requestRepository.countByStatus(Request.Status.REJECTED));
        stats.setCancelledRequests(requestRepository.countByStatus(Request.Status.CANCELLED));

        // Requests by department
        List<Map<String, Object>> byDept = employees.stream()
                .collect(Collectors.groupingBy(User::getDepartment, Collectors.counting()))
                .entrySet().stream()
                .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("dept", e.getKey()); m.put("count", e.getValue()); return m; })
                .collect(Collectors.toList());
        stats.setRequestsByDept(byDept);

        // Monthly trend (static demo data — in production query by month)
        stats.setMonthlyTrend(buildMonthlyTrend());

        return stats;
    }

    private List<Map<String, Object>> buildMonthlyTrend() {
        String[] months = {"Nov","Dec","Jan","Feb","Mar","Apr"};
        List<Map<String, Object>> trend = new java.util.ArrayList<>();
        for (String month : months) {
            Map<String, Object> m = new HashMap<>();
            m.put("month", month);
            m.put("requests", (long)(Math.random() * 20 + 15));
            m.put("approved", (long)(Math.random() * 15 + 10));
            trend.add(m);
        }
        return trend;
    }

    // ─── Mapper ───────────────────────────────────────────────

    private UserResponse toUserResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setEmployeeId(user.getEmployeeId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole().name());
        r.setDepartment(user.getDepartment());
        r.setActive(user.isActive());
        if (user.getManager() != null) {
            r.setManagerId(user.getManager().getId());
            r.setManagerName(user.getManager().getName());
        }
        return r;
    }
}
