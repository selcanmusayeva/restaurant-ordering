package com.ordering.restaurant.repository;

import com.ordering.restaurant.model.Category;
import com.ordering.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByMenuIdAndAvailableTrue(Long menuId);
    List<MenuItem> findByCategoryAndAvailableTrue(Category category);
    List<MenuItem> findByMenuId(Long menuId);
    List<MenuItem> findByCategory(Category category);
    List<MenuItem> findByAvailableTrue();
    List<MenuItem> findByNameContainingIgnoreCase(String name);
} 