package com.orderflow.service;

import com.orderflow.domain.Order;
import com.orderflow.domain.OrderStatus;
import com.orderflow.dto.OrderDTO;
import com.orderflow.dto.OrderItemDTO;
import com.orderflow.repository.CustomerRepository;
import com.orderflow.repository.ItemRepository;
import com.orderflow.repository.OrderRepository;
import com.orderflow.service.workflow.OrderWorkflowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.orderflow.domain.Customer;
import com.orderflow.domain.Item;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceCreateTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderWorkflowService workflowService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    public void testCreateOrder() {
        // Prepare test data
        Long customerId = 1L;
        Long itemId = 100L;

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(customerId);
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setItemId(itemId);
        itemDTO.setQuantity(2);
        orderDTO.setItems(Collections.singletonList(itemDTO));

        Customer customer = new Customer();
        customer.setId(customerId);

        Item item = new Item();
        item.setId(itemId);
        item.setPrice(new BigDecimal("50.00"));

        // Mock behaviors
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L); // Simulate DB ID generation
            return order;
        });

        // Execute
        Order result = orderService.createOrder(orderDTO);

        // Verify
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        verify(orderRepository).save(any(Order.class));
        verify(workflowService).startOrderProcess(result.getId());
    }
}
