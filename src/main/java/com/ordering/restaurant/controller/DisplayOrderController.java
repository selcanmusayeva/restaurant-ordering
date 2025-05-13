package com.ordering.restaurant.controller;

import com.ordering.restaurant.model.Category;
import com.ordering.restaurant.model.MenuItem;
import com.ordering.restaurant.repository.CategoryRepository;
import com.ordering.restaurant.repository.MenuItemRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/display-order")
@Tag(name = "Display Order", description = "Endpoints for managing display order of categories and menu items")
public class DisplayOrderController {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public DisplayOrderController(CategoryRepository categoryRepository,
            MenuItemRepository menuItemRepository) {
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @PostMapping("/categories")
    @Operation(summary = "Update the display order of categories")
    public ResponseEntity<?> updateCategoryOrder(@RequestBody List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        for (int i = 0; i < categoryIds.size(); i++) {
            final int order = i;
            final Long categoryId = categoryIds.get(i);
            categories.stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .ifPresent(category -> category.setDisplayOrder(order));
        }

        categoryRepository.saveAll(categories);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/menu-items/{categoryId}")
    @Operation(summary = "Update the display order of menu items within a specific category")
    public ResponseEntity<?> updateMenuItemOrder(
            @PathVariable Long categoryId,
            @RequestBody List<Long> menuItemIds) {

        if (!categoryRepository.existsById(categoryId)) {
            return ResponseEntity.badRequest().body("Category not found");
        }

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);

        if (menuItems.stream().anyMatch(item -> !item.getCategory().getId().equals(categoryId))) {
            return ResponseEntity.badRequest().body("Some menu items do not belong to the specified category");
        }

        for (int i = 0; i < menuItemIds.size(); i++) {
            final int order = i;
            final Long menuItemId = menuItemIds.get(i);
            menuItems.stream()
                    .filter(m -> m.getId().equals(menuItemId))
                    .findFirst()
                    .ifPresent(menuItem -> menuItem.setDisplayOrder(order));
        }

        menuItemRepository.saveAll(menuItems);
        return ResponseEntity.ok().build();
    }
}
