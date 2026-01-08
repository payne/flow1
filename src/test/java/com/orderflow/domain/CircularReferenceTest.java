package com.orderflow.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CircularReferenceTest {

    @Test
    public void testInventoryItemHashCode() {
        Item item = new Item();
        Inventory inventory = new Inventory();

        item.setInventory(inventory);
        inventory.setItem(item);

        // This should throw StackOverflowError if the bug exists
        assertDoesNotThrow(() -> item.hashCode());
        assertDoesNotThrow(() -> inventory.hashCode());
    }
}
