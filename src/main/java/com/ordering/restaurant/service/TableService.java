package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.QRCodeResponse;
import com.ordering.restaurant.dto.TableDTO;
import com.ordering.restaurant.exception.ResourceNotFoundException;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.model.RestaurantTable;
import com.ordering.restaurant.model.TableAssignment;
import com.ordering.restaurant.model.User;
import com.ordering.restaurant.repository.OrderRepository;
import com.ordering.restaurant.repository.TableAssignmentRepository;
import com.ordering.restaurant.repository.TableRepository;
import com.ordering.restaurant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TableAssignmentRepository tableAssignmentRepository;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public TableService(TableRepository tableRepository,
                       OrderRepository orderRepository,
                       UserRepository userRepository,
                       TableAssignmentRepository tableAssignmentRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.tableAssignmentRepository = tableAssignmentRepository;
    }

    public List<TableDTO> getAllTables() {
        return tableRepository.findAll().stream()
                .map(TableDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TableDTO getTableById(Long id) {
        return tableRepository.findById(id)
                .map(TableDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
    }

    @Transactional
    public TableDTO createTable(TableDTO tableDTO) {
        RestaurantTable table = tableDTO.toEntity();
        String qrCodeData = UUID.randomUUID().toString();
        table.setQrCode(qrCodeData);
        if (table.getStatus() == null) {
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
        }
        
        RestaurantTable savedTable = tableRepository.save(table);
        return TableDTO.fromEntity(savedTable);
    }

    @Transactional
    public TableDTO updateTable(Long id, TableDTO tableDTO) {
        RestaurantTable existingTable = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
        
        existingTable.setTableNumber(tableDTO.getTableNumber());
        existingTable.setCapacity(tableDTO.getCapacity());
        
        if (tableDTO.getStatus() != null) {
            existingTable.setStatus(tableDTO.getStatus());
        }
        
        RestaurantTable updatedTable = tableRepository.save(existingTable);
        return TableDTO.fromEntity(updatedTable);
    }

    @Transactional
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Table not found with id: " + id);
        }
        
        if (hasActiveOrders(id)) {
            throw new IllegalStateException("Cannot delete table with active orders");
        }
        
        // Also remove any table assignments
        List<TableAssignment> assignments = tableAssignmentRepository.findByTableIdAndActiveTrue(id);
        for (TableAssignment assignment : assignments) {
            assignment.setActive(false);
        }
        tableAssignmentRepository.saveAll(assignments);
        
        tableRepository.deleteById(id);
    }

    public boolean hasActiveOrders(Long tableId) {
        List<Order> activeOrders = orderRepository.findActiveOrdersByTable(tableId);
        return !activeOrders.isEmpty();
    }

    @Transactional
    public QRCodeResponse generateQRCode(Long tableId) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + tableId));
        
        String qrCodeData = UUID.randomUUID().toString();
        table.setQrCode(qrCodeData);
        tableRepository.save(table);
        
        String qrCodeUrl = frontendUrl + "/table/" + tableId + "?code=" + qrCodeData;
        
        return QRCodeResponse.builder()
                .tableId(table.getId())
                .tableNumber(table.getTableNumber())
                .qrCodeData(qrCodeData)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }
    
    public List<TableDTO> getTablesAssignedToWaiter(String username) {
        User waiter = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        List<TableAssignment> assignments = tableAssignmentRepository.findByWaiterAndActiveTrue(waiter);
        return assignments.stream()
                .map(assignment -> TableDTO.fromEntity(assignment.getTable()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void assignTableToWaiter(Long tableId, String waiterUsername, LocalDateTime shiftDate) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + tableId));
                
        User waiter = userRepository.findByUsername(waiterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + waiterUsername));
                
        // Deactivate existing assignments for this table
        List<TableAssignment> existingAssignments = tableAssignmentRepository.findByTableIdAndActiveTrue(tableId);
        for (TableAssignment assignment : existingAssignments) {
            assignment.setActive(false);
        }
        tableAssignmentRepository.saveAll(existingAssignments);
        
        // Create new assignment
        TableAssignment newAssignment = new TableAssignment();
        newAssignment.setTable(table);
        newAssignment.setWaiter(waiter);
        newAssignment.setShiftDate(shiftDate);
        newAssignment.setAssignedAt(LocalDateTime.now());
        newAssignment.setActive(true);
        
        tableAssignmentRepository.save(newAssignment);
    }
    
    @Transactional
    public void unassignTableFromWaiter(Long tableId) {
        List<TableAssignment> assignments = tableAssignmentRepository.findByTableIdAndActiveTrue(tableId);
        for (TableAssignment assignment : assignments) {
            assignment.setActive(false);
        }
        tableAssignmentRepository.saveAll(assignments);
    }
} 