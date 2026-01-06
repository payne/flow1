package com.orderflow.repository;

import com.orderflow.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Inventory entity operations.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByItemId(Long itemId);

    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable <= i.reorderLevel")
    List<Inventory> findLowStockItems();
}
