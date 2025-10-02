package com.imfine.ngs.order.dto;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderDetails;
import com.imfine.ngs.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

@Builder
@Getter
public class OrderResponseDto {
  private Long orderId;

  private Long userId;

  private List<OrderDetailsResponseDto> orderDetails;

  private OrderStatus status;

  private String merchantUid;
}
