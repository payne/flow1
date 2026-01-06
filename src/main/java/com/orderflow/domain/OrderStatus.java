package com.orderflow.domain;

/**
 * Order status enum representing the current state of an order.
 * Status transitions are managed by the Flowable BPMN processes.
 */
public enum OrderStatus {
    PENDING,
    VALIDATING,
    VALIDATION_FAILED,
    PAYMENT_PROCESSING,
    PAYMENT_FAILED,
    PAYMENT_COMPLETED,
    FULFILLING,
    FULFILLMENT_FAILED,
    SHIPPING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
