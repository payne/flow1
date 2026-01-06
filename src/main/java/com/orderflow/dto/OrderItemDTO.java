package com.orderflow.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for order items.
 */
@Data
public class OrderItemDTO {

    private Long itemId;
    private String itemName;
    private String itemSku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
