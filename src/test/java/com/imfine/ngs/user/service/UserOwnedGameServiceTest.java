package com.imfine.ngs.user.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.service.search.GameSearchService;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.support.entity.OrderDetail;
import com.imfine.ngs.support.entity.OrderDetailEntity;
import com.imfine.ngs.support.repository.OrderDetailRepository;
import com.imfine.ngs.support.service.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserOwnedGameServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    private GameSearchService gameSearchService;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @BeforeEach
    void setUp() {
        long userId = 1L;
        long userId2 = 2L;

        gameRepository.save(Game.builder()
                .isActive(true)
                .name("It Takes Two")
                .price(25000L)
                .build()
        );

        gameRepository.save(Game.builder()
                .isActive(true)
                .name("Split Fiction")
                .price(54000L)
                .build()
        );

        gameRepository.save(Game.builder()
                .isActive(true)
                .name("test Takes Two")
                .price(25000L)
                .build()
        );

        gameRepository.save(Game.builder()
                .isActive(true)
                .name("test Fiction")
                .price(54000L)
                .build()
        );

        OrderDetailEntity entity = new OrderDetailEntity(
                OrderDetail.builder()
                .orderId(1L)
                .gameId(100L)
                .build()
        );
        orderDetailRepository.save(entity);

        OrderDetailEntity entity2 = new OrderDetailEntity(
                OrderDetail.builder()
                        .orderId(2L)
                        .gameId(100L)
                        .build()
        );

        orderDetailRepository.save(entity2);
    }

    @Test
    @DisplayName("getGameListByUserId: 사용자가 소유한 게임 목록을 가지고 온다.")
    void getGameListByUserId() {
        // given
        long userId = 1L;
        List<Order> orders = orderService.getOrdersByUserId(userId);

        // when
        List<Long> orderIds = orders.stream().map(Order::getOrderId).toList();
        List<Game> games = new ArrayList<>();


        List<OrderDetailEntity> orderDetails = orderDetailService.findByIdOrderIdIn(orderIds);
        System.out.println(orderDetailService.findGameByOrderId(1L));

        for (OrderDetailEntity orderDetail : orderDetails) {
            System.out.println("gameID" + orderDetail.getId().getGameId());
            games.add(gameSearchService.findById(orderDetail.getId().getGameId()));
        }

        System.out.println("GAME:");
        games.forEach(System.out::println);

        // then
        // 1. userId가 1일 때 orderIds의 개수가 2이여야한다.
        assertThat(orderIds.size()).isEqualTo(2);
        // 2. orderId를 가지고 와서 orderDetail의 테이블과 비교 필요
        assertThat(games.size()).isEqualTo(orders.size());
    }
}