package com.workflow.approval.service;

import com.workflow.approval.dto.request.CreateEmployeeRequest;
import com.workflow.approval.dto.request.MasterDataRequest;
import com.workflow.approval.dto.response.UserResponse;
import com.workflow.approval.entity.Department;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.DepartmentRepository;
import com.workflow.approval.repository.RequestRepository;
import com.workflow.approval.repository.RequestTypeRepository;
import com.workflow.approval.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock UserRepository userRepository;
    @Mock DepartmentRepository departmentRepository;
    @Mock RequestTypeRepository requestTypeRepository;
    @Mock RequestRepository requestRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;

    @InjectMocks AdminService adminService;

    private User manager;
    private User employee;
    private CreateEmployeeRequest createReq;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setId(1L);
        manager.setEmployeeId("MGR001");
        manager.setName("Veerakumar");
        manager.setEmail("veerakumar@gmail.com");
        manager.setRole(User.Role.MANAGER);
        manager.setDepartment("HR");
        manager.setActive(true);

        employee = new User();
        employee.setId(2L);
        employee.setEmployeeId("EMP001");
        employee.setName("Karthik");
        employee.setEmail("karthik.com");
        employee.setRole(User.Role.EMPLOYEE);
        employee.setDepartment("IT");
        employee.setActive(true);
        employee.setManager(manager);

        createReq = new CreateEmployeeRequest();
        createReq.setEmployeeId("EMP002");
        createReq.setName("Raji");
        createReq.setEmail("Raji@gmail.com");
        createReq.setDepartment("Operations");
        createReq.setRole("EMPLOYEE");
        createReq.setManagerId(1L);
        createReq.setPassword("Raji@123");
    }

    @Test
    void getAllEmployees_returnsNonAdminUsers() {
        User admin = new User(); admin.setRole(User.Role.ADMIN);
        when(userRepository.findAll()).thenReturn(Arrays.asList(manager, employee, admin));

        List<UserResponse> result = adminService.getAllEmployees();

        assertEquals(2, result.size());
    }


    

    @Test
    void createEmployee_withDuplicateEmployeeId_throwsBadRequest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP002")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> adminService.createEmployee(createReq));
    }

    @Test
    void createEmployee_withoutPassword_throwsBadRequest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmployeeId(anyString())).thenReturn(false);
        createReq.setPassword("");
        assertThrows(BadRequestException.class, () -> adminService.createEmployee(createReq));
    }

    @Test
    void createEmployee_employeeWithoutManager_throwsBadRequest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmployeeId(anyString())).thenReturn(false);
        createReq.setManagerId(null);
        assertThrows(BadRequestException.class, () -> adminService.createEmployee(createReq));
    }

    @Test
    void toggleActive_deactivatesActiveUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(userRepository.save(any())).thenReturn(employee);

        UserResponse result = adminService.toggleActive(2L);

        verify(userRepository).save(argThat(u -> !((User) u).isActive()));
    }

    @Test
    void toggleActive_withInvalidId_throwsNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> adminService.toggleActive(999L));
    }

    @Test
    void createDepartment_withDuplicateName_throwsBadRequest() {
        MasterDataRequest req = new MasterDataRequest();
        req.setName("IT");
        when(departmentRepository.existsByName("IT")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> adminService.createDepartment(req));
    }


}
