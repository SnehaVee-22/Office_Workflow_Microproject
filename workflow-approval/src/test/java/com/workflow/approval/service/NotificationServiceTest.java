package com.workflow.approval.service;

import com.workflow.approval.dto.response.NotificationResponse;
import com.workflow.approval.entity.Notification;
import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.NotificationRepository;
import com.workflow.approval.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepository;
    @Mock UserRepository userRepository;

    @InjectMocks NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("karthik@gmail.com");
        user.setName("Karthik");

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setMessage("Your request REQ0001 has been APPROVED.");
        notification.setType("APPROVED");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createNotification_savesNotification() {
        Request req = new Request();
        notificationService.createNotification(user, "Test message", "PENDING", req);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotificationsForUser_returnsUserNotifications() {
        when(userRepository.findByEmail("karthik@gmail.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(Arrays.asList(notification));

        List<NotificationResponse> result = notificationService.getNotificationsForUser("karthik@gmail.com");

        assertEquals(1, result.size());
        assertEquals("APPROVED", result.get(0).getType());
        assertFalse(result.get(0).isRead());
    }

}
