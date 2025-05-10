package com.ordering.restaurant.repository;

import com.ordering.restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTableId(Long tableId);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByTableIdAndStatus(Long tableId, Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Order> findActiveOrdersByTable(@Param("tableId") Long tableId);
    
    List<Order> findByStatusIn(List<Order.OrderStatus> statuses);
    List<Order> findByCreatedAtAfter(LocalDateTime dateTime);
    List<Order> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
} 