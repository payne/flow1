package com.orderflow.service;

import com.orderflow.domain.Inventory;
import com.orderflow.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for inventory management.
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Optional<Inventory> getInventoryByItemId(Long itemId) {
        return inventoryRepository.findByItemId(itemId);
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    @Override
    public Inventory updateInventory(Long inventoryId, Integer quantityToAdd) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + inventoryId));

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantityToAdd);
        inventory.setLastRestockedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    @Override
    public boolean checkAvailability(Long itemId, Integer quantity) {
        Optional<Inventory> inventory = inventoryRepository.findByItemId(itemId);
        return inventory.isPresent() && inventory.get().getQuantityAvailable() >= quantity;
    }

    @Override
    public void reserveInventory(Long itemId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for item: " + itemId));

        if (inventory.getQuantityAvailable() < quantity) {
            throw new RuntimeException("Insufficient inventory for item: " + itemId);
        }

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - quantity);
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    public void releaseInventory(Long itemId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for item: " + itemId));

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
        inventory.setQuantityReserved(inventory.getQuantityReserved() - quantity);

        inventoryRepository.save(inventory);
    }
}
