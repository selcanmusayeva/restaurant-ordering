package com.ordering.restaurant.repository;

import com.ordering.restaurant.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByTableNumber(String tableNumber);
    List<RestaurantTable> findByStatus(RestaurantTable.TableStatus status);
    Optional<RestaurantTable> findByQrCode(String qrCode);
} 