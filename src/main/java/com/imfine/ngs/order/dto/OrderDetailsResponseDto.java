package com.imfine.ngs.order.dto;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.order.entity.OrderDetails;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
public class OrderDetailsResponseDto {
  private Long id;

  private GameCardResponse game;

  private long priceSnapshot;
}
