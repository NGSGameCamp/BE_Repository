package com.imfine.ngs.order.entity;

public enum OrderStatus {
    PRE_ORDERED,
    PENDING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    PURCHASED_CONFIRMED,
    REFUND_REQUESTED,
    PARTIALLY_REFUNDED,
    FULLY_REFUNDED
}
