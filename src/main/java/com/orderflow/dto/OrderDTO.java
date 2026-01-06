package com.orderflow.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating orders.
 */
@Data
public class OrderDTO {

    private Long customerId;
    private String customerEmail;
    private String customerFirstName;
    private String customerLastName;

    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
    private String shippingCountry;

    private String paymentMethod;
    private String notes;

    private List<OrderItemDTO> items = new ArrayList<>();

    private BigDecimal totalAmount;
}
