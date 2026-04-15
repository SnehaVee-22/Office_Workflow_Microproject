package com.workflow.approval.service;

import com.workflow.approval.dto.request.CreateRequestDto;
import com.workflow.approval.dto.response.RequestResponse;
import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.RequestRepository;
import com.workflow.approval.repository.UserRepository;
import com.workflow.approval.util.RequestIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock RequestRepository requestRepository;
    @Mock UserRepository userRepository;
    @Mock NotificationService notificationService;
    @Mock RequestIdGenerator requestIdGenerator;

    @InjectMocks EmployeeService employeeService;

    private User employee;
    private User manager;
    private Request pendingRequest;
    private CreateRequestDto leaveDto;
    private CreateRequestDto softwareDto;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setId(1L); manager.setName("Veerakumar");
        manager.setEmail("veerakumar@gmail.com"); manager.setRole(User.Role.MANAGER);

        employee = new User();
        employee.setId(2L); employee.setEmployeeId("EMP001");
        employee.setName("Karthik"); employee.setEmail("karthik@gmail.com");
        employee.setRole(User.Role.EMPLOYEE); employee.setDepartment("IT");
        employee.setManager(manager); employee.setActive(true);

        pendingRequest = new Request();
        pendingRequest.setId(10L);
        pendingRequest.setRequestId("REQ0001");
        pendingRequest.setEmployee(employee);
        pendingRequest.setManager(manager);
        pendingRequest.setRequestType("Leave Request");
        pendingRequest.setLeaveType("CL");
        pendingRequest.setDuration("Full Day");
        pendingRequest.setLeavePlan("Planned");
        pendingRequest.setDescription("Doctor appointment");
        pendingRequest.setStatus(Request.Status.PENDING);

        leaveDto = new CreateRequestDto();
        leaveDto.setRequestType("Leave Request");
        leaveDto.setLeaveType("CL");
        leaveDto.setDuration("Full Day");
        leaveDto.setLeavePlan("Planned");
        leaveDto.setStartDate("2024-04-15");
        leaveDto.setDescription("Annual leave");

        softwareDto = new CreateRequestDto();
        softwareDto.setRequestType("Software Requirement");
        softwareDto.setSoftwareName("Figma");
        softwareDto.setSoftwareReason("Design work");
        softwareDto.setDescription("Need Figma for UI project");
    }

    @Test
    void getMyRequests_returnsEmployeeRequests() {
        when(userRepository.findByEmail("karthik@gmail.com")).thenReturn(Optional.of(employee));
        when(requestRepository.findByEmployeeOrderByCreatedAtDesc(employee))
                .thenReturn(Arrays.asList(pendingRequest));

        List<RequestResponse> result = employeeService.getMyRequests("karthik@gmail.com");

        assertEquals(1, result.size());
        assertEquals("REQ0001", result.get(0).getRequestId());
    }

    @Test
    void createRequest_leaveRequest_savesSuccessfully() {
        when(userRepository.findByEmail("karthik@gmail.com")).thenReturn(Optional.of(employee));
        when(requestIdGenerator.generate()).thenReturn("REQ0002");
        when(requestRepository.save(any())).thenReturn(pendingRequest);

        RequestResponse result = employeeService.createRequest("karthik@gmail.com", leaveDto);

        assertNotNull(result);
        verify(requestRepository, times(1)).save(any());
        verify(notificationService, times(1)).createNotification(any(), anyString(), eq("PENDING"), any());
    }

    @Test
    void cancelRequest_pendingRequest_cancelsSuccessfully() {
        when(requestRepository.findById(10L)).thenReturn(Optional.of(pendingRequest));
        when(requestRepository.save(any())).thenReturn(pendingRequest);

        RequestResponse result = employeeService.cancelRequest(10L, "karthik@gmail.com");

        verify(requestRepository).save(argThat(r -> ((Request) r).getStatus() == Request.Status.CANCELLED));
    }

    @Test
    void cancelRequest_approvedRequest_throwsBadRequest() {
        pendingRequest.setStatus(Request.Status.APPROVED);
        when(requestRepository.findById(10L)).thenReturn(Optional.of(pendingRequest));
        assertThrows(BadRequestException.class,
                () -> employeeService.cancelRequest(10L, "karthik@gmail.com"));
    }

   

    @Test
    void updateRequest_nonPendingRequest_throwsBadRequest() {
        pendingRequest.setStatus(Request.Status.APPROVED);
        when(requestRepository.findById(10L)).thenReturn(Optional.of(pendingRequest));
        assertThrows(BadRequestException.class,
                () -> employeeService.updateRequest(10L, "karthik@gmail.com", leaveDto));
    }

}
