package com.imfine.ngs.order;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("removal")
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("사용자가 고른 게임을 기반으로 주문을 생성할 수 있다.")
    void createOrderFromGameList() {
        //given
        long userId = 1;
        Game game1 = Game.builder()
                .name("It Takes Two")
                .price(25000L)
                .build();
        Game game2 = Game.builder()
                .name("Split Fiction")
                .price(65000L)
                .build();
        List<Game> games = Arrays.asList(game1, game2);

        //when
        Order newOrder = orderService.createOrder(userId, games);

        //then
        assertThat(newOrder).isNotNull();
        assertThat(newOrder.getUserId()).isEqualTo(userId);
        assertThat(newOrder.getTotalPrice()).isEqualTo(90000);
        assertThat(newOrder.getOrderItemCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 게임 목록으로는 주문을 생성할 수 없다.")
    void createOrderWithEmptyGameList() {
        //given
        long userId = 1;
        List<Game> emptyGames = new ArrayList<>();

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(userId, emptyGames));
    }

    @Test
    @DisplayName("모든 주문 목록을 정상적으로 반환한다.")
    void getAllOrdersReturnsAllOrders() {
        //given
        long userId1 = 1;
        long userId2 = 2;

        Game game1 = Game.builder()
                .name("Game1")
                .price(1000L)
                .build();

        Game game2 = Game.builder()
                .name("Game2")
                .price(2000L)
                .build();

        Game game3 = Game.builder()
                .name("Game 3")
                .price(3000L)
                .build();

        Order order1 = orderService.createOrder(userId1, Arrays.asList(game1));
        Order order2 = orderService.createOrder(userId1, Arrays.asList(game2));
        Order order3 = orderService.createOrder(userId2, Arrays.asList(game3));

        //when
        List<Order> allOrders = orderService.getAllOrders();

        //then
        assertThat(allOrders).isNotNull();
        assertThat(allOrders.size()).isEqualTo(3);
        assertThat(allOrders).contains(order1, order2, order3);
    }

    @Test
    @DisplayName("비어있는 주문에 게임을 추가하면 아이템 개수는 1이 된다.")
    void addGameToEmptyOrder() {
        //given
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(0);
        Game newGame = Game.builder()
                            .name("Overcooked")
                            .price(22000L)
                            .build();

        //when
        orderService.addGameToOrder(order, newGame);

        //then
        assertThat(order.getOrderItemCount()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(22000);
    }

    @Test
    @DisplayName("아이템이 있는 주문에 게임을 삭제하면 아이템 개수는 0이 된다.")
    void removeGameFromOrder() {
        //given
        Order order = new Order();
        Game gameToRemove = Game.builder()
                                .name("It takes two")
                                .price(25000L)
                                .build();
        order.setOrderItems(new ArrayList<>(Arrays.asList(gameToRemove)));
        order.setTotalPrice(25000);

        //when
        orderService.removeGameFromOrder(order, gameToRemove);

        //then
        assertThat(order.getOrderItemCount()).isEqualTo(0);
        assertThat(order.getTotalPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("한 주문에는 같은 게임을 중복해서 담을 수 없다.")
    void addDuplicateGameToOrder() {
        //given
        Order order = new Order();
        Game newGame = Game.builder()
                            .name("AmongUs")
                            .price(5500L)
                            .build();

        order.setOrderItems(new ArrayList<>(Arrays.asList(newGame)));
        order.setTotalPrice(5500);

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.addGameToOrder(order, newGame));
        assertThat(order.getOrderItemCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문에 여러 개의 다른 게임을 추가하면, 추가한 개수만큼 아이템이 늘어난다.")
    void addMultipleGamesToOrder() {
        //given
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(0);
        Game game1 = Game.builder()
                          .name("PICO PARK")
                          .price(5500L)
                          .build();

        Game game2 = Game.builder()
                         .name("Split Fiction")
                         .price(65000L)
                         .build();

        //when
        orderService.addGameToOrder(order, game1);
        orderService.addGameToOrder(order, game2);

        //then
        assertThat(order.getOrderItemCount()).isEqualTo(2);
        assertThat(order.getTotalPrice()).isEqualTo(70500);
    }

    @Test
    @DisplayName("이미 삭제된 게임은 주문에서 삭제할 수 없다.")
    void removeNonExistentGameFromOrder() {
        //given
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(0);
        Game nonExistentGame = Game.builder()
                                    .name("Stardew Valley")
                                    .price(16500L)
                                    .build();

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.removeGameFromOrder(order, nonExistentGame));
    }

    @Test
    @DisplayName("한 사용자는 여러 개의 주문을 가질 수 있다.")
    void userCanHaveMultipleOrders() {
        //given
        long userId = 1;
        Game game1 = Game.builder()
                .name("Game1")
                .price(1000L)
                .build();

        Game game2 = Game.builder()
                .name("Game2")
                .price(2000L)
                .build();

        Game game3 = Game.builder()
                .name("Game 3")
                .price(3000L)
                .build();

        Order order1 = orderService.createOrder(userId, List.of(game1));
        Order order2 = orderService.createOrder(userId, Arrays.asList(game2, game3));

        //when
        List<Order> userOrders = orderService.getOrdersByUserId(userId);

        //then
        assertThat(userOrders).isNotNull();
        assertThat(userOrders.size()).isEqualTo(2);
        assertThat(userOrders).contains(order1, order2);
    }

    @Test
    @DisplayName("특정 사용자의 주문 목록을 정상적으로 반환한다.")
    void getOrdersByUserIdReturnsCorrectOrders() {
        //given
        long userId1 = 1;
        long userId2 = 2;

        Game game1 = Game.builder()
                .name("Game1")
                .price(1000L)
                .build();

        Game game2 = Game.builder()
                .name("Game2")
                .price(2000L)
                .build();

        Game game3 = Game.builder()
                .name("Game 3")
                .price(3000L)
                .build();

        Game game4 = Game.builder()
                .name("Game4")
                .price(400L)
                .build();

        Order order1 = orderService.createOrder(userId1, Arrays.asList(game1, game2, game3));
        Order order2 = orderService.createOrder(userId2, List.of(game4));

        //when
        List<Order> user1Orders = orderService.getOrdersByUserId(userId1);
        List<Order> user2Orders = orderService.getOrdersByUserId(userId2);
        List<Order> nonExistentUserOrders = orderService.getOrdersByUserId(0000);

        //then
        assertThat(user1Orders).isNotNull();
        assertThat(user1Orders.size()).isEqualTo(1);
        assertThat(user1Orders).contains(order1);

        assertThat(user2Orders).isNotNull();
        assertThat(user2Orders.size()).isEqualTo(1);
        assertThat(user2Orders).contains(order2);

        assertThat(nonExistentUserOrders).isNotNull();
        assertThat(nonExistentUserOrders).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID 조회 시 예외가 발생한다.")
    void findByNonExistentOrderId() {
        //given
        long nonExistentOrderId = 9999L;

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.findByOrderId(nonExistentOrderId));
    }
}
