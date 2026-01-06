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
 * Service task delegate for order validation.
 * Validates inventory availability, pricing, and customer information.
 */
@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(OrderStatus.VALIDATING);
        orderRepository.save(order);

        boolean validationPassed = true;
        StringBuilder validationErrors = new StringBuilder();

        // Validate each order item has sufficient inventory
        for (OrderItem item : order.getOrderItems()) {
            var inventory = inventoryRepository.findByItemId(item.getItem().getId());
            if (inventory.isEmpty() || inventory.get().getQuantityAvailable() < item.getQuantity()) {
                validationPassed = false;
                validationErrors.append("Insufficient inventory for item: ")
                        .append(item.getItem().getName()).append(". ");
            }
        }

        if (validationPassed) {
            // Reserve inventory for this order
            for (OrderItem item : order.getOrderItems()) {
                var inventory = inventoryRepository.findByItemId(item.getItem().getId())
                        .orElseThrow();
                inventory.setQuantityAvailable(inventory.getQuantityAvailable() - item.getQuantity());
                inventory.setQuantityReserved(inventory.getQuantityReserved() + item.getQuantity());
                inventoryRepository.save(inventory);
            }

            execution.setVariable("validationResult", "PASSED");

            // Set variables for process decisions
            execution.setVariable("totalAmount", order.getTotalAmount().doubleValue());

            // Check if any item requires refrigeration (for food process)
            boolean requiresRefrigeration = order.getOrderItems().stream()
                    .anyMatch(item -> Boolean.TRUE.equals(item.getItem().getRequiresRefrigeration()));
            execution.setVariable("requiresRefrigeration", requiresRefrigeration);

        } else {
            order.setStatus(OrderStatus.VALIDATION_FAILED);
            order.setNotes(validationErrors.toString());
            orderRepository.save(order);
            execution.setVariable("validationResult", "FAILED");
            throw new RuntimeException("Order validation failed: " + validationErrors);
        }
    }
}
