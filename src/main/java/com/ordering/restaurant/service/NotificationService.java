package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.NotificationDTO;
import com.ordering.restaurant.exception.ResourceNotFoundException;
import com.ordering.restaurant.model.Notification;
import com.ordering.restaurant.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDTO> getPendingNotificationsForUser(String username) {
        return notificationRepository.findByTargetUsernameAndReadFalse(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        
        notification.setRead(true);
        return convertToDTO(notificationRepository.save(notification));
    }

    @Transactional
    public void createNotification(String message, String type, Long referenceId, 
                                String referenceType, String targetUsername) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setTargetUsername(targetUsername);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }

    @Transactional
    public void createOrderReadyNotification(Long orderId, String waiterUsername) {
        createNotification(
                "Order #" + orderId + " is ready for delivery",
                Notification.NotificationType.ORDER_READY.name(),
                orderId,
                "ORDER",
                waiterUsername
        );
    }

    @Transactional
    public void createNewOrderNotification(Long orderId, String kitchenStaffUsername) {
        createNotification(
                "New order #" + orderId + " has been placed",
                Notification.NotificationType.NEW_ORDER.name(),
                orderId,
                "ORDER",
                kitchenStaffUsername
        );
    }

    @Transactional
    public void createTableRequestNotification(Long tableId, String request, String waiterUsername) {
        createNotification(
                "Table #" + tableId + " requests: " + request,
                Notification.NotificationType.TABLE_REQUEST.name(),
                tableId,
                "TABLE",
                waiterUsername
        );
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .targetUsername(notification.getTargetUsername())
                .build();
    }
} 