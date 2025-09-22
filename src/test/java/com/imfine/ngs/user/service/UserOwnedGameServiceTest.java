package com.imfine.ngs.user.service;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserOwnedGameServiceTest {

    /*
    @Autowired
    OrderService orderService;

    @BeforeEach
    void setUp() {
        String userId = "1";
        String userId2 = "2";

        com.imfine.ngs.order.entity.Game game1 = new com.imfine.ngs.order.entity.Game("It Takes Two", 25000);
        com.imfine.ngs.order.entity.Game game2 = new com.imfine.ngs.order.entity.Game("Split Fiction", 54000);
        com.imfine.ngs.order.entity.Game game3 = new com.imfine.ngs.order.entity.Game("test Takes Two", 25000);
        com.imfine.ngs.order.entity.Game game4 = new com.imfine.ngs.order.entity.Game("test Fiction", 54000);

        List<Game> games = Arrays.asList(game1, game2);
        orderService.createOrder(userId, games);

        List<Game> games2 = Arrays.asList(game1, game2, game3, game4);
        orderService.createOrder(userId, games2);
    }

    @Test
    @DisplayName("getGameListByUserId: 사용자가 소유한 게임 목록을 가지고 온다.")
    void getGameListByUserId() {
        // given
        String userId = "1";
        long orderId = 1L;
        long gameId = 1L;
        List<Order> orders = orderService.getOrdersByUserId(userId);

        // when
        List<Long> orderIds = orders.stream().map(Order::getOrderId).toList();

        // then
        // 1. userId가 1일 때 orderIds의 개수가 2이여야한다.
        assertThat(orderIds.size()).isEqualTo(2);
        // 2. orderId를 가지고 와서 orderDetail의 테이블과 비교 필요


    }
}

class OrderDetail {
    void orderDetail(Order order, Game game) {
    }


     */
}