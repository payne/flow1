package com.orderflow.service;

import com.orderflow.domain.*;
import com.orderflow.dto.OrderDTO;
import com.orderflow.dto.OrderItemDTO;
import com.orderflow.repository.CustomerRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

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

    private Order order;
    private Customer customer;
    private Item item;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("john.doe@example.com");

        item = new Item();
        item.setId(100L);
        item.setPrice(BigDecimal.valueOf(10.0));

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);

        orderDTO = new OrderDTO();
        orderDTO.setCustomerId(1L);
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setItemId(100L);
        itemDTO.setQuantity(2);
        orderDTO.setItems(Arrays.asList(itemDTO));
    }

    @Test
    public void testCreateOrder_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        Order createdOrder = orderService.createOrder(orderDTO);

        assertNotNull(createdOrder);
        assertEquals(customer.getId(), createdOrder.getCustomer().getId());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(1, createdOrder.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(20.0), createdOrder.getTotalAmount());

        verify(workflowService, times(1)).startOrderProcess(1L);
    }

    @Test
    public void testCreateOrder_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderDTO);
        });

        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    public void testCreateOrder_ItemNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderDTO);
        });

        assertTrue(exception.getMessage().contains("Item not found"));
    }

    @Test
    public void testCreateOrder_NewCustomer() {
        orderDTO.setCustomerId(null);
        orderDTO.setCustomerEmail("new.user@example.com");
        orderDTO.setCustomerFirstName("New");
        orderDTO.setCustomerLastName("User");

        when(customerRepository.findByEmail("new.user@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(2L);
            return c;
        });
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        Order createdOrder = orderService.createOrder(orderDTO);

        assertNotNull(createdOrder);
        assertEquals(2L, createdOrder.getCustomer().getId());
    }

    @Test
    public void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.getOrderById(1L);

        assertTrue(foundOrder.isPresent());
        assertEquals(1L, foundOrder.get().getId());
    }

    @Test
    public void testGetOrderByOrderNumber() {
        when(orderRepository.findByOrderNumber("ORD-123")).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.getOrderByOrderNumber("ORD-123");

        assertTrue(foundOrder.isPresent());
    }

    @Test
    public void testGetOrdersByCustomerId() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByCustomerId(1L)).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrdersByCustomerId(1L);

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
    }

    @Test
    public void testGetAllOrders() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAllOrderByCreatedAtDesc()).thenReturn(orders);

        List<Order> foundOrders = orderService.getAllOrders();

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
    }

    @Test
    public void testGetOrdersByStatus() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
    }

    @Test
    public void testUpdateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, updatedOrder.getStatus());
    }

    @Test
    public void testUpdateOrderStatus_Completed() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, updatedOrder.getStatus());
        assertNotNull(updatedOrder.getCompletedAt());
    }

    @Test
    public void testUpdateOrderStatus_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    public void testCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getCompletedAt());
    }
}
