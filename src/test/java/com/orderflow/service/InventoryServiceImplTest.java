package com.orderflow.service;

import com.orderflow.domain.Inventory;
import com.orderflow.domain.Item;
import com.orderflow.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;
    private Item item;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setId(100L);
        item.setName("Test Item");

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setItem(item);
        inventory.setQuantityAvailable(50);
        inventory.setQuantityReserved(0);
    }

    @Test
    public void testGetInventoryByItemId() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.of(inventory));

        Optional<Inventory> foundInventory = inventoryService.getInventoryByItemId(100L);

        assertTrue(foundInventory.isPresent());
        assertEquals(100L, foundInventory.get().getItem().getId());
    }

    @Test
    public void testGetAllInventory() {
        List<Inventory> inventories = Arrays.asList(inventory);
        when(inventoryRepository.findAll()).thenReturn(inventories);

        List<Inventory> foundInventories = inventoryService.getAllInventory();

        assertNotNull(foundInventories);
        assertEquals(1, foundInventories.size());
    }

    @Test
    public void testGetLowStockItems() {
        List<Inventory> lowStockItems = Arrays.asList(inventory);
        when(inventoryRepository.findLowStockItems()).thenReturn(lowStockItems);

        List<Inventory> result = inventoryService.getLowStockItems();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testUpdateInventory_Success() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory updatedInventory = inventoryService.updateInventory(1L, 10);

        assertNotNull(updatedInventory);
        assertEquals(60, updatedInventory.getQuantityAvailable());
        assertNotNull(updatedInventory.getLastRestockedAt());
    }

    @Test
    public void testUpdateInventory_NotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.updateInventory(1L, 10);
        });

        assertTrue(exception.getMessage().contains("Inventory not found"));
    }

    @Test
    public void testCheckAvailability_Available() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.of(inventory));

        boolean available = inventoryService.checkAvailability(100L, 10);

        assertTrue(available);
    }

    @Test
    public void testCheckAvailability_NotAvailable() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.of(inventory));

        boolean available = inventoryService.checkAvailability(100L, 100);

        assertFalse(available);
    }

    @Test
    public void testCheckAvailability_ItemNotFound() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.empty());

        boolean available = inventoryService.checkAvailability(100L, 10);

        assertFalse(available);
    }

    @Test
    public void testReserveInventory_Success() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        inventoryService.reserveInventory(100L, 10);

        assertEquals(40, inventory.getQuantityAvailable());
        assertEquals(10, inventory.getQuantityReserved());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    public void testReserveInventory_InsufficientStock() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.of(inventory));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveInventory(100L, 100);
        });

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    public void testReserveInventory_ItemNotFound() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveInventory(100L, 10);
        });

        assertTrue(exception.getMessage().contains("Inventory not found"));
    }

    @Test
    public void testReleaseInventory_Success() {
        inventory.setQuantityAvailable(40);
        inventory.setQuantityReserved(10);
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        inventoryService.releaseInventory(100L, 10);

        assertEquals(50, inventory.getQuantityAvailable());
        assertEquals(0, inventory.getQuantityReserved());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    public void testReleaseInventory_ItemNotFound() {
        when(inventoryRepository.findByItemId(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.releaseInventory(100L, 10);
        });

        assertTrue(exception.getMessage().contains("Inventory not found"));
    }
}
