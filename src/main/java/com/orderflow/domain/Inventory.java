package com.orderflow.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Inventory entity managing stock levels for items.
 * Supports quantity available and quantity reserved for orders in process.
 */
@Entity
@Table(name = "inventory", schema = "app_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Item item;

    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable = 0;

    @Column(name = "quantity_reserved", nullable = false)
    private Integer quantityReserved = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity = 50;

    @Column(name = "warehouse_location", length = 50)
    private String warehouseLocation;

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if this item is low on stock.
     * @return true if quantity available is at or below reorder level
     */
    public boolean isLowStock() {
        return quantityAvailable <= reorderLevel;
    }

    /**
     * Get total quantity (available + reserved).
     * @return total quantity in inventory
     */
    public Integer getTotalQuantity() {
        return quantityAvailable + quantityReserved;
    }
}
