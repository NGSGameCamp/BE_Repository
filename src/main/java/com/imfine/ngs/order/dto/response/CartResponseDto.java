package com.imfine.ngs.order.dto.response;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartResponseDto {

    private final Long orderId;
    private final Long userId;
    private final List<CartItemDto> orderDetails;
    private final OrderStatus status;
    private final long totalPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CartResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUserId();
        this.orderDetails = order.getOrderDetails().stream()
                .map(CartItemDto::new)
                .collect(Collectors.toList());
        this.status = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
