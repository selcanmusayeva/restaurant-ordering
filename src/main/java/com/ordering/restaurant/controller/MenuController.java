package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.CategoryDTO;
import com.ordering.restaurant.dto.MenuDTO;
import com.ordering.restaurant.dto.MenuItemDTO;
import com.ordering.restaurant.model.MenuItem;
import com.ordering.restaurant.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu")
@Tag(name = "Menu", description = "Menu management endpoints")
public class MenuController {

    @Autowired
    private MenuService menuService;
    
    private final Path uploadPath = Paths.get("uploads/images");
    
    public MenuController() {
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @GetMapping
    @Operation(summary = "Get all active menus")
    public ResponseEntity<List<MenuDTO>> getAllActiveMenus() {
        return ResponseEntity.ok(menuService.getAllActiveMenus());
    }

    @PostMapping
    @Operation(summary = "Create a new menu")
    public ResponseEntity<MenuDTO> createMenu(@Valid @RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(menuService.createMenu(menuDTO));
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "Get specific menu item by ID")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @PostMapping("/items")
    @Operation(summary = "Create a new menu item")
    public ResponseEntity<MenuItemDTO> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO) {
        return ResponseEntity.ok(menuService.createMenuItem(menuItemDTO));
    }
    
    @PostMapping(value = "/items/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image for menu item")
    public ResponseEntity<MenuItemDTO> uploadMenuItemImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {
        
        MenuItemDTO menuItem = menuService.getMenuItemById(id);
        
        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path targetLocation = uploadPath.resolve(fileName);
        
        // Save the file
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Update the menu item
        menuItem.setImageUrl("/uploads/images/" + fileName);
        menuItem.setImageName(file.getOriginalFilename());
        menuItem.setImageContentType(file.getContentType());
        menuItem.setImageSize(file.getSize());
        
        return ResponseEntity.ok(menuService.updateMenuItem(id, menuItem));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update menu item")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, menuItemDTO));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete menu item")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all menu categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(menuService.getAllCategories());
    }

    @GetMapping("/categories/{categoryId}/items")
    @Operation(summary = "Get menu items by category")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuService.getMenuItemsByCategory(categoryId));
    }

    @PostMapping("/categories")
    @Operation(summary = "Add new category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(menuService.createCategory(categoryDTO));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(menuService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        menuService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/available")
    @Operation(summary = "Get available menu items")
    public ResponseEntity<List<MenuItemDTO>> getAvailableItems() {
        return ResponseEntity.ok(menuService.getAvailableItems());
    }
} 