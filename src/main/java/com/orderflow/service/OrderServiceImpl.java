package com.orderflow.service;

import com.orderflow.domain.*;
import com.orderflow.dto.OrderDTO;
import com.orderflow.dto.OrderItemDTO;
import com.orderflow.repository.CustomerRepository;
import com.orderflow.repository.ItemRepository;
import com.orderflow.repository.OrderRepository;
import com.orderflow.service.workflow.OrderWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for order management.
 * Integrates with Flowable workflows for order processing.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderWorkflowService workflowService;

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        // Find or create customer
        Customer customer;
        if (orderDTO.getCustomerId() != null) {
            customer = customerRepository.findById(orderDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found: " + orderDTO.getCustomerId()));
        } else if (orderDTO.getCustomerEmail() != null) {
            customer = customerRepository.findByEmail(orderDTO.getCustomerEmail())
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setEmail(orderDTO.getCustomerEmail());
                        newCustomer.setFirstName(orderDTO.getCustomerFirstName());
                        newCustomer.setLastName(orderDTO.getCustomerLastName());
                        return customerRepository.save(newCustomer);
                    });
        } else {
            throw new RuntimeException("Customer ID or email required");
        }

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddressLine1(orderDTO.getShippingAddressLine1());
        order.setShippingAddressLine2(orderDTO.getShippingAddressLine2());
        order.setShippingCity(orderDTO.getShippingCity());
        order.setShippingState(orderDTO.getShippingState());
        order.setShippingZipCode(orderDTO.getShippingZipCode());
        order.setShippingCountry(orderDTO.getShippingCountry());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setNotes(orderDTO.getNotes());

        // Create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Item item = itemRepository.findById(itemDTO.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemDTO.getItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(item.getPrice());
            orderItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        // Save order
        order = orderRepository.save(order);

        // Start workflow process
        workflowService.startOrderProcess(order.getId());

        return order;
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(status);

        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            order.setCompletedAt(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /**
     * Generate unique order number.
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
}
