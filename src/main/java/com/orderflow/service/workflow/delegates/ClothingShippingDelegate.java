package com.orderflow.service.workflow.delegates;

import com.orderflow.domain.Order;
import com.orderflow.domain.OrderStatus;
import com.orderflow.domain.Shipment;
import com.orderflow.repository.OrderRepository;
import com.orderflow.repository.ShipmentRepository;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service task delegate for clothing shipping.
 * Standard shipping with return label included.
 */
@Component("clothingShippingDelegate")
public class ClothingShippingDelegate implements JavaDelegate {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long orderId = (Long) execution.getVariable("orderId");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(OrderStatus.SHIPPING);
        orderRepository.save(order);

        // Create shipment record
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setTrackingNumber("CLO-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        shipment.setCarrier("USPS");
        shipment.setShippingMethod("STANDARD_WITH_RETURN_LABEL");
        shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(5));
        shipment.setShippedAt(LocalDateTime.now());

        shipmentRepository.save(shipment);

        order.setStatus(OrderStatus.SHIPPED);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);

        execution.setVariable("shippingResult", "SUCCESS");
        execution.setVariable("trackingNumber", shipment.getTrackingNumber());
    }
}
