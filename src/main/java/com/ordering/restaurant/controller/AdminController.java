package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.UserDTO;
import com.ordering.restaurant.model.UserRole;
import com.ordering.restaurant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Administrative endpoints")
@PreAuthorize("hasRole('MANAGER')")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/staff")
    @Operation(summary = "Create a new staff user (Manager, Chef, Waiter)")
    public ResponseEntity<UserDTO> createStaffUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getRole() == null
                || (userDTO.getRole() != UserRole.MANAGER
                && userDTO.getRole() != UserRole.CHEF
                && userDTO.getRole() != UserRole.WAITER)) {
            throw new IllegalArgumentException("Role must be MANAGER, CHEF, or WAITER");
        }

        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {

        if (role == null) {
            throw new IllegalArgumentException("Role must be specified");
        }

        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }
}
