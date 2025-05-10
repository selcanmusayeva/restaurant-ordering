package com.ordering.restaurant.repository;

import com.ordering.restaurant.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetUsernameAndReadFalse(String username);
    List<Notification> findByTargetUsername(String username);
    List<Notification> findByReferenceIdAndReferenceType(Long referenceId, String referenceType);
} 