package com.orderflow.service.workflow.delegates;

import com.orderflow.domain.Order;
import com.orderflow.domain.OrderStatus;
import com.orderflow.repository.OrderRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Service task delegate for payment processing.
 * Simulates payment processing and updates order status.
 */
@Component("processPaymentDelegate")
public class ProcessPaymentDelegate implements JavaDelegate {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(OrderStatus.PAYMENT_PROCESSING);
        orderRepository.save(order);

        // Simulate payment processing
        boolean paymentSuccess = processPayment(order);

        if (paymentSuccess) {
            order.setStatus(OrderStatus.PAYMENT_COMPLETED);
            order.setPaymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            orderRepository.save(order);
            execution.setVariable("paymentResult", "SUCCESS");
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderRepository.save(order);
            execution.setVariable("paymentResult", "FAILED");
            throw new RuntimeException("Payment processing failed for order: " + orderId);
        }
    }

    /**
     * Simulates payment processing.
     * In a real application, this would integrate with a payment gateway.
     */
    private boolean processPayment(Order order) {
        // Simulate payment processing - always succeed for demo
        // In production, integrate with Stripe, PayPal, etc.
        try {
            Thread.sleep(1000); // Simulate processing delay
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
