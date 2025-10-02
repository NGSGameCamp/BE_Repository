package com.imfine.ngs.order.dto.mapper;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.order.dto.OrderDetailsResponseDto;
import com.imfine.ngs.order.dto.OrderResponseDto;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
  private final GameCardMapper gameMapper;

  public OrderResponseDto toOrderResponseDto(Order order) {
    return OrderResponseDto.builder()
            .orderId(order.getOrderId())
            .userId(order.getUserId())
            .orderDetails(order.getOrderDetails()
                    .stream()
                    .map(this::toOrderDetailsResponseDto)
                    .toList())
            .status(order.getStatus())
            .merchantUid(order.getMerchantUid())
            .build();
  }

  public OrderDetailsResponseDto toOrderDetailsResponseDto(OrderDetails order) {
    return OrderDetailsResponseDto.builder()
            .id(order.getId())
            .game(gameMapper.toCardResponse(order.getGame()))
            .priceSnapshot(order.getPriceSnapshot())
            .build();
  }


}
