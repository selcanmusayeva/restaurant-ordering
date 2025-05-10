package com.ordering.restaurant.repository;

import com.ordering.restaurant.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByActiveTrue();
    Optional<Menu> findByName(String name);
} 