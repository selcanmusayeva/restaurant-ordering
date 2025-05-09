package com.ordering.restaurant.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "table_assignments")
@Data
@NoArgsConstructor
public class TableAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User waiter;
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();
    
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    @Column(name = "shift_date", nullable = false)
    private LocalDateTime shiftDate;
} 