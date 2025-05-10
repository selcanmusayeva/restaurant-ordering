package com.ordering.restaurant.repository;

import com.ordering.restaurant.model.TableAssignment;
import com.ordering.restaurant.model.RestaurantTable;
import com.ordering.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TableAssignmentRepository extends JpaRepository<TableAssignment, Long> {
    List<TableAssignment> findByWaiterAndActiveTrue(User waiter);
    List<TableAssignment> findByWaiterUsernameAndActiveTrue(String username);
    List<TableAssignment> findByTableAndActiveTrue(RestaurantTable table);
    List<TableAssignment> findByTableIdAndActiveTrue(Long tableId);
    List<TableAssignment> findByShiftDateAndActiveTrue(LocalDateTime shiftDate);
} 