package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.CategoryDTO;
import com.ordering.restaurant.dto.MenuDTO;
import com.ordering.restaurant.dto.MenuItemDTO;
import com.ordering.restaurant.exception.ResourceNotFoundException;
import com.ordering.restaurant.model.Category;
import com.ordering.restaurant.model.Menu;
import com.ordering.restaurant.model.MenuItem;
import com.ordering.restaurant.repository.CategoryRepository;
import com.ordering.restaurant.repository.MenuItemRepository;
import com.ordering.restaurant.repository.MenuRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuRepository menuRepository, 
                      MenuItemRepository menuItemRepository,
                      CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "activeMenus", unless = "#result.isEmpty()")
    public List<MenuDTO> getAllActiveMenus() {
        return menuRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"activeMenus", "menuItems", "availableItems"}, allEntries = true)
    public MenuDTO createMenu(MenuDTO menuDTO) {
        Menu menu = new Menu();
        menu.setName(menuDTO.getName());
        menu.setDescription(menuDTO.getDescription());
        menu.setActive(menuDTO.isActive());
        return convertToDTO(menuRepository.save(menu));
    }

    @Cacheable(value = "menuItems", key = "#id", unless = "#result == null")
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return convertToDTO(menuItem);
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "availableItems", "categoryMenuItems"}, allEntries = true)
    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        
        Category category = categoryRepository.findById(menuItemDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + menuItemDTO.getCategoryId()));
        menuItem.setCategory(category);
        menuItem.setCategoryName(category.getName());
        
        Menu menu = menuRepository.findById(menuItemDTO.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + menuItemDTO.getMenuId()));
        menuItem.setMenu(menu);
        
        menuItem.setAvailable(menuItemDTO.isAvailable());
        menuItem.setImageUrl(menuItemDTO.getImageUrl());
        menuItem.setImageName(menuItemDTO.getImageName());
        menuItem.setImageContentType(menuItemDTO.getImageContentType());
        menuItem.setImageSize(menuItemDTO.getImageSize());
        
        return convertToDTO(menuItemRepository.save(menuItem));
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "availableItems", "categoryMenuItems"}, allEntries = true)
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        
        Category category = categoryRepository.findById(menuItemDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + menuItemDTO.getCategoryId()));
        menuItem.setCategory(category);
        menuItem.setCategoryName(category.getName());
        
        Menu menu = menuRepository.findById(menuItemDTO.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + menuItemDTO.getMenuId()));
        menuItem.setMenu(menu);
        
        menuItem.setAvailable(menuItemDTO.isAvailable());
        menuItem.setImageUrl(menuItemDTO.getImageUrl());
        menuItem.setImageName(menuItemDTO.getImageName());
        menuItem.setImageContentType(menuItemDTO.getImageContentType());
        menuItem.setImageSize(menuItemDTO.getImageSize());
        
        return convertToDTO(menuItemRepository.save(menuItem));
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "availableItems", "categoryMenuItems"}, allEntries = true)
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    @Cacheable(value = "categories", unless = "#result.isEmpty()")
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categoryMenuItems", key = "#categoryId", unless = "#result.isEmpty()")
    public List<MenuItemDTO> getMenuItemsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                
        return menuItemRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryMenuItems"}, allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDisplayOrder(categoryDTO.getDisplayOrder());
        category.setDescription(categoryDTO.getDescription());
        category.setActive(categoryDTO.isActive());
        
        return convertToDTO(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryMenuItems"}, allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if the name is being changed to an existing one
        if (!category.getName().equals(categoryDTO.getName()) && 
            categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        
        category.setName(categoryDTO.getName());
        category.setDisplayOrder(categoryDTO.getDisplayOrder());
        category.setDescription(categoryDTO.getDescription());
        category.setActive(categoryDTO.isActive());
        category.setUpdatedAt(LocalDateTime.now());
        
        return convertToDTO(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryMenuItems"}, allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
                
        // Check if category has menu items
        if (!category.getMenuItems().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated menu items");
        }
        
        categoryRepository.delete(category);
    }

    @Cacheable(value = "availableItems", unless = "#result.isEmpty()")
    public List<MenuItemDTO> getAvailableItems() {
        return menuItemRepository.findByAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menu.getId());
        menuDTO.setName(menu.getName());
        menuDTO.setDescription(menu.getDescription());
        menuDTO.setActive(menu.isActive());
        return menuDTO;
    }
    
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDisplayOrder(category.getDisplayOrder());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setActive(category.isActive());
        return categoryDTO;
    }

    private MenuItemDTO convertToDTO(MenuItem menuItem) {
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setId(menuItem.getId());
        menuItemDTO.setName(menuItem.getName());
        menuItemDTO.setDescription(menuItem.getDescription());
        menuItemDTO.setPrice(menuItem.getPrice());
        menuItemDTO.setCategoryId(menuItem.getCategory().getId());
        menuItemDTO.setCategoryName(menuItem.getCategoryName());
        menuItemDTO.setAvailable(menuItem.isAvailable());
        menuItemDTO.setImageUrl(menuItem.getImageUrl());
        menuItemDTO.setImageName(menuItem.getImageName());
        menuItemDTO.setImageContentType(menuItem.getImageContentType());
        menuItemDTO.setDisplayOrder(menuItem.getDisplayOrder());
        menuItemDTO.setImageSize(menuItem.getImageSize());
        if (menuItem.getMenu() != null) {
            menuItemDTO.setMenuId(menuItem.getMenu().getId());
        }
        return menuItemDTO;
    }
} 