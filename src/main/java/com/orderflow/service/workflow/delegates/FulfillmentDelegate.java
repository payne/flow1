package com.orderflow.service.workflow.delegates;

import com.orderflow.domain.Order;
import com.orderflow.domain.OrderItem;
import com.orderflow.domain.OrderStatus;
import com.orderflow.repository.InventoryRepository;
import com.orderflow.repository.OrderRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service task delegate for order fulfillment.
 * Picks, packs, and prepares the order for shipping.
 */
@Component("fulfillmentDelegate")
public class FulfillmentDelegate implements JavaDelegate {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(OrderStatus.FULFILLING);
        orderRepository.save(order);

        try {
            // Simulate fulfillment process
            fulfillOrder(order);

            // Move inventory from reserved to shipped
            for (OrderItem item : order.getOrderItems()) {
                var inventory = inventoryRepository.findByItemId(item.getItem().getId())
                        .orElseThrow();
                inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
                inventoryRepository.save(inventory);
            }

            execution.setVariable("fulfillmentResult", "SUCCESS");

        } catch (Exception e) {
            order.setStatus(OrderStatus.FULFILLMENT_FAILED);
            orderRepository.save(order);
            execution.setVariable("fulfillmentResult", "FAILED");
            throw new RuntimeException("Fulfillment failed for order: " + orderId, e);
        }
    }

    /**
     * Simulates order fulfillment process.
     * In a real application, this would integrate with warehouse management system.
     */
    private void fulfillOrder(Order order) {
        // Simulate picking and packing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
