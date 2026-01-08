package com.orderflow.service;

import com.orderflow.domain.Order;
import com.orderflow.domain.OrderStatus;
import com.orderflow.dto.OrderApprovalDTO;
import com.orderflow.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for order operations.
 */
public interface OrderService {

    Order createOrder(OrderDTO orderDTO);

    Optional<Order> getOrderById(Long id);

    Optional<Order> getOrderByOrderNumber(String orderNumber);

    List<Order> getOrdersByCustomerId(Long customerId);

    List<Order> getAllOrders();

    List<Order> getOrdersByStatus(OrderStatus status);

    Order updateOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);

    void approveOrder(Long orderId, String taskId, OrderApprovalDTO approvalDTO);
}
