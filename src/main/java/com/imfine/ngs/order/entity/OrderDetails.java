package com.imfine.ngs.order.entity;

import com.imfine.ngs._global.entity.BaseTimeEntity;
import com.imfine.ngs.game.entity.Game;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetails extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    private long priceSnapshot; // 구매 시점의 게임 가격

    public OrderDetails(Order order, Game game) {
        this.order = order;
        this.game = game;
        this.priceSnapshot = game.getPrice(); // 생성 시점의 게임 가격을 저장
    }
}
