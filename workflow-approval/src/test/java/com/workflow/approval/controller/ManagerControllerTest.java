package com.workflow.approval.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.approval.dto.request.DecisionRequest;
import com.workflow.approval.dto.response.RequestResponse;
import com.workflow.approval.exception.BadRequestException;
import com.workflow.approval.exception.GlobalExceptionHandler;
import com.workflow.approval.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock ManagerService managerService;
    @InjectMocks ManagerController managerController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private Authentication managerAuth;
    private RequestResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        managerAuth = new UsernamePasswordAuthenticationToken(
                "veerakumar@gmail.com", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );

        sampleResponse = new RequestResponse();
        sampleResponse.setId(1L);
        sampleResponse.setRequestId("REQ0001");
        sampleResponse.setEmployeeName("karthik");
        sampleResponse.setRequestType("Leave Request");
        sampleResponse.setLeaveType("CL");
        sampleResponse.setStatus("PENDING");
    }


    @Test
    void approveRequest_withRemarks_returns200() throws Exception {
        sampleResponse.setStatus("APPROVED");
        sampleResponse.setManagerRemarks("Approved!");
        when(managerService.approveRequest(eq(1L), eq("veerakumar@gmail.com"), any()))
                .thenReturn(sampleResponse);

        DecisionRequest decision = new DecisionRequest();
        decision.setRemarks("Approved!");

        mockMvc.perform(patch("/manager/requests/1/approve")
                .principal(managerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void rejectRequest_withRemarks_returns200() throws Exception {
        sampleResponse.setStatus("REJECTED");
        sampleResponse.setManagerRemarks("No balance.");
        when(managerService.rejectRequest(eq(1L), eq("veerakumar@gmail.com"), any()))
                .thenReturn(sampleResponse);

        DecisionRequest decision = new DecisionRequest();
        decision.setRemarks("No balance.");

        mockMvc.perform(patch("/manager/requests/1/reject")
                .principal(managerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    

    @Test
    void getHistory_withDateRange_returns200() throws Exception {
        sampleResponse.setStatus("APPROVED");
        when(managerService.getApprovalHistory(any(), any(), any()))
                .thenReturn(Arrays.asList(sampleResponse));

        mockMvc.perform(get("/manager/requests/history")
                .param("startDate", "2024-04-01")
                .param("endDate", "2024-04-30")
                .principal(managerAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getPendingRequests_emptyList_returns200WithEmptyArray() throws Exception {
        when(managerService.getPendingRequests("veerakumar@gmail.com")).thenReturn(List.of());

        mockMvc.perform(get("/manager/requests/pending").principal(managerAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
