package com.imfine.ngs.order.dto.response;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.env.Env;
import com.imfine.ngs.game.enums.GameStatus;
import com.imfine.ngs.order.entity.OrderDetails;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartItemDto {

    private final Long id; // game id
    private final String name;
    private final long price; // 원래 가격
    private final List<String> env;
    private final long priceSnapshot; // 담은 시점의 가격

    public CartItemDto(OrderDetails orderDetails) {
        Game game = orderDetails.getGame();
        this.id = game.getId();
        this.name = game.getName();
        this.price = game.getPrice();
        this.env = game.getEnv().stream()
                .map(env -> env.getEnv().getEnvType().toString())
                .collect(Collectors.toList());
        this.priceSnapshot = orderDetails.getPriceSnapshot();
    }
}
