package com.orderflow.domain;

/**
 * Item category enum that maps to specific BPMN processes.
 * Each category has its own workflow with unique approval and shipping rules.
 */
public enum ItemCategory {
    ELECTRONICS("electronics-order-process"),
    CLOTHING("clothing-order-process"),
    FOOD("food-order-process");

    private final String processKey;

    ItemCategory(String processKey) {
        this.processKey = processKey;
    }

    /**
     * Gets the Flowable BPMN process key for this category.
     * @return The process definition key used to start the workflow
     */
    public String getProcessKey() {
        return processKey;
    }
}
