package com.orderflow.dto;

import lombok.Data;

/**
 * DTO for order approval actions.
 */
@Data
public class OrderApprovalDTO {

    private Boolean approved;
    private String comments;
}
