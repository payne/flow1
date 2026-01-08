package com.orderflow.service;

import com.orderflow.domain.*;
import com.orderflow.dto.OrderDTO;
import com.orderflow.dto.OrderItemDTO;
import com.orderflow.repository.CustomerRepository;
import com.orderflow.repository.InventoryRepository;
import com.orderflow.repository.ItemRepository;
import com.orderflow.repository.OrderRepository;
import com.orderflow.service.workflow.OrderWorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodOrderTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderWorkflowService workflowService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Test
    public void testCreateFoodOrder() {
        // Prepare mock data
        Item foodItem = new Item();
        foodItem.setId(1L);
        foodItem.setName("Apple");
        foodItem.setPrice(BigDecimal.valueOf(1.50));
        foodItem.setCategory(ItemCategory.FOOD);

        // Link Inventory to trigger potential circular reference if not fixed
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setItem(foodItem);
        foodItem.setInventory(inventory);

        // Ensure hashCode can be called without error
        foodItem.hashCode();
        inventory.hashCode();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(foodItem));

        // Mock Customer finding/creation
        when(customerRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(100L);
            return savedOrder;
        });

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerEmail("test@example.com");
        orderDTO.setCustomerFirstName("Test");
        orderDTO.setCustomerLastName("User");
        orderDTO.setShippingAddressLine1("123 Food St");
        orderDTO.setShippingCity("Foodville");
        orderDTO.setShippingState("CA");
        orderDTO.setShippingZipCode("90210");
        orderDTO.setShippingCountry("USA");
        orderDTO.setPaymentMethod("Credit Card");

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setItemId(1L);
        itemDTO.setQuantity(5);

        orderDTO.setItems(Collections.singletonList(itemDTO));

        // Act
        Order createdOrder = orderService.createOrder(orderDTO);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(100L, createdOrder.getId());
        assertEquals(1, createdOrder.getOrderItems().size());

        verify(workflowService).startOrderProcess(100L);
    }
}
