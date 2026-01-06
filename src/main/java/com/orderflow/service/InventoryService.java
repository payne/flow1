package com.orderflow.service;

import com.orderflow.domain.Inventory;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for inventory operations.
 */
public interface InventoryService {

    Optional<Inventory> getInventoryByItemId(Long itemId);

    List<Inventory> getAllInventory();

    List<Inventory> getLowStockItems();

    Inventory updateInventory(Long inventoryId, Integer quantityToAdd);

    boolean checkAvailability(Long itemId, Integer quantity);

    void reserveInventory(Long itemId, Integer quantity);

    void releaseInventory(Long itemId, Integer quantity);
}
