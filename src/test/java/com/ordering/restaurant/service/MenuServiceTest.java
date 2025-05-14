package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.CategoryDTO;
import com.ordering.restaurant.dto.MenuDTO;
import com.ordering.restaurant.dto.MenuItemDTO;
import com.ordering.restaurant.model.Category;
import com.ordering.restaurant.model.Menu;
import com.ordering.restaurant.model.MenuItem;
import com.ordering.restaurant.repository.CategoryRepository;
import com.ordering.restaurant.repository.MenuItemRepository;
import com.ordering.restaurant.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private MenuService menuService;

    private Menu testMenu;
    private Category testCategory;
    private MenuItem testMenuItem;
    private MenuDTO menuDTO;
    private CategoryDTO categoryDTO;
    private MenuItemDTO menuItemDTO;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Category Description");

        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setName("Test Item");
        testMenuItem.setDescription("Test Item Description");
        testMenuItem.setPrice(new BigDecimal("9.99"));
        testMenuItem.setCategory(testCategory);
        testMenuItem.setAvailable(true);

        testMenu = new Menu();
        testMenu.setId(1L);
        testMenu.setName("Test Menu");
        testMenu.setDescription("Test Menu Description");
        testMenu.setItems(Arrays.asList(testMenuItem));

        menuDTO = new MenuDTO();
        menuDTO.setName("Test Menu");
        menuDTO.setDescription("Test Menu Description");

        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setDescription("Test Category Description");

        menuItemDTO = new MenuItemDTO();
        menuItemDTO.setName("Test Item");
        menuItemDTO.setDescription("Test Item Description");
        menuItemDTO.setPrice(new BigDecimal("9.99"));
        menuItemDTO.setCategoryId(1L);
        menuItemDTO.setAvailable(true);
    }

    @Test
    void createMenu_ValidRequest_ReturnsCreatedMenu() {
        // Given
        when(menuRepository.save(any())).thenReturn(testMenu);

        // When
        MenuDTO result = menuService.createMenu(menuDTO);

        // Then
        assertNotNull(result);
        assertEquals(testMenu.getName(), result.getName());
        assertEquals(testMenu.getDescription(), result.getDescription());
        verify(menuRepository).save(any());
    }

    @Test
    void createCategory_ValidRequest_ReturnsCreatedCategory() {
        // Given
        when(categoryRepository.save(any())).thenReturn(testCategory);

        // When
        CategoryDTO result = menuService.createCategory(categoryDTO);

        // Then
        assertNotNull(result);
        assertEquals(testCategory.getName(), result.getName());
        assertEquals(testCategory.getDescription(), result.getDescription());
        verify(categoryRepository).save(any());
    }

    @Test
    void createMenuItem_ValidRequest_ReturnsCreatedMenuItem() {
        // Given
        when(categoryRepository.findById(any())).thenReturn(Optional.of(testCategory));
        when(menuItemRepository.save(any())).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.createMenuItem(menuItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(testMenuItem.getName(), result.getName());
        assertEquals(testMenuItem.getDescription(), result.getDescription());
        assertEquals(testMenuItem.getPrice(), result.getPrice());
        verify(menuItemRepository).save(any());
    }

    @Test
    void createMenuItem_InvalidCategory_ThrowsException() {
        // Given
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> menuService.createMenuItem(menuItemDTO));
    }

    @Test
    void updateMenuItem_ValidRequest_ReturnsUpdatedMenuItem() {
        // Given
        when(menuItemRepository.findById(any())).thenReturn(Optional.of(testMenuItem));
        when(menuItemRepository.save(any())).thenReturn(testMenuItem);

        // When
        MenuItemDTO result = menuService.updateMenuItem(1L, menuItemDTO);

        // Then
        assertNotNull(result);
        assertEquals(testMenuItem.getName(), result.getName());
        assertEquals(testMenuItem.getDescription(), result.getDescription());
        verify(menuItemRepository).save(any());
    }

    @Test
    void updateMenuItem_InvalidMenuItem_ThrowsException() {
        // Given
        when(menuItemRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> menuService.updateMenuItem(1L, menuItemDTO));
    }

    @Test
    void getMenuItemById_ExistingMenuItem_ReturnsMenuItem() {
        // Given
        when(menuItemRepository.findById(any())).thenReturn(Optional.of(testMenuItem));

        // When
        MenuItemDTO result = menuService.getMenuItemById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testMenuItem.getName(), result.getName());
        assertEquals(testMenuItem.getDescription(), result.getDescription());
    }

    @Test
    void getMenuItemById_NonExistingMenuItem_ThrowsException() {
        // Given
        when(menuItemRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> menuService.getMenuItemById(1L));
    }

    @Test
    void getAvailableItems_ReturnsAvailableMenuItemList() {
        // Given
        when(menuItemRepository.findByAvailableTrue()).thenReturn(Arrays.asList(testMenuItem));

        // When
        List<MenuItemDTO> results = menuService.getAvailableItems();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testMenuItem.getName(), results.get(0).getName());
        assertTrue(results.get(0).isAvailable());
    }
} 