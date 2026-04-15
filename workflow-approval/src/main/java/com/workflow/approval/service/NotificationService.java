package com.workflow.approval.service;

import com.workflow.approval.dto.response.NotificationResponse;
import com.workflow.approval.entity.Notification;
import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.User;
import com.workflow.approval.exception.ResourceNotFoundException;
import com.workflow.approval.repository.NotificationRepository;
import com.workflow.approval.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                                UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createNotification(User user, String message, String type, Request request) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRequest(request);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotificationsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Notification not found");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setMessage(n.getMessage());
        r.setType(n.getType());
        r.setRead(n.isRead());
        r.setCreatedAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
        return r;
    }
}
