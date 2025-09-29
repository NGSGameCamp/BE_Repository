//package com.imfine.ngs.order;
//
//import com.imfine.ngs._global.error.exception.BusinessException;
//import com.imfine.ngs._global.error.model.ErrorCode;
//import com.imfine.ngs.game.entity.Game;
//import com.imfine.ngs.game.repository.GameRepository;
//import com.imfine.ngs.order.entity.Order;
//import com.imfine.ngs.order.entity.OrderStatus;
//import com.imfine.ngs.order.repository.OrderRepository;
//import com.imfine.ngs.order.service.OrderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SuppressWarnings("removal")
//@SpringBootTest
//@Transactional
//public class OrderServiceTest {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    private Long testUserId;
//    private Game game1;
//    private Game game2;
//
//    @BeforeEach
//    void setUp() {
//        testUserId = 1L;
//
//        // 테스트용 게임 저장
//        game1 = gameRepository.save(Game.builder().name("Test Game 1").price(10000L).isActive(true).build());
//        game2 = gameRepository.save(Game.builder().name("Test Game 2").price(20000L).isActive(true).build());
//    }
//
//    @Test
//    @DisplayName("사용자의 장바구니를 조회하거나 새로 생성할 수 있다.")
//    void getOrCreateCart() {
//        // When
//        Order cart = orderService.getOrCreateCart(testUserId);
//
//        // Then
//        assertThat(cart).isNotNull();
//        assertThat(cart.getUserId()).isEqualTo(testUserId);
//        assertThat(cart.getStatus()).isEqualTo(OrderStatus.PENDING);
//        assertThat(cart.getOrderDetails()).isEmpty();
//
//        // When - 다시 호출하면 기존 장바구니 반환
//        Order existingCart = orderService.getOrCreateCart(testUserId);
//        assertThat(existingCart.getOrderId()).isEqualTo(cart.getOrderId());
//    }
//
//    @Test
//    @DisplayName("장바구니에 게임을 추가할 수 있다.")
//    void addGameToCart() {
//        // Given
//        Order cart = orderService.getOrCreateCart(testUserId);
//
//        // When
//        Order updatedCart = orderService.addGameToCart(testUserId, game1.getId());
//
//        // Then
//        assertThat(updatedCart.getOrderDetails()).hasSize(1);
//        assertThat(updatedCart.getOrderDetails().get(0).getGame().getId()).isEqualTo(game1.getId());
//        assertThat(updatedCart.getTotalPrice()).isEqualTo(game1.getPrice());
//
//        // When - 다른 게임 추가
//        updatedCart = orderService.addGameToCart(testUserId, game2.getId());
//
//        // Then
//        assertThat(updatedCart.getOrderDetails()).hasSize(2);
//        assertThat(updatedCart.getTotalPrice()).isEqualTo(game1.getPrice() + game2.getPrice());
//    }
//
//    @Test
//    @DisplayName("이미 장바구니에 담긴 게임은 다시 추가할 수 없다.")
//    void addDuplicateGameToCartThrowsException() {
//        // Given
//        orderService.addGameToCart(testUserId, game1.getId());
//
//        // When & Then
//        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.addGameToCart(testUserId, game1.getId()));
//        assertEquals(ErrorCode.GAME_ALREADY_IN_CART, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게임은 장바구니에 추가할 수 없다.")
//    void addNonExistentGameToCartThrowsException() {
//        // Given
//        Long nonExistentGameId = 9999L;
//
//        // When & Then
//        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.addGameToCart(testUserId, nonExistentGameId));
//        assertEquals(ErrorCode.ENTITY_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("장바구니에서 게임을 제거할 수 있다.")
//    void removeGameFromCart() {
//        // Given
//        orderService.addGameToCart(testUserId, game1.getId());
//        orderService.addGameToCart(testUserId, game2.getId());
//        Order cart = orderService.getOrCreateCart(testUserId);
//        assertThat(cart.getOrderDetails()).hasSize(2);
//
//        // When
//        Order updatedCart = orderService.removeGameFromCart(testUserId, game1.getId());
//
//        // Then
//        assertThat(updatedCart.getOrderDetails()).hasSize(1);
//        assertThat(updatedCart.getOrderDetails().get(0).getGame().getId()).isEqualTo(game2.getId());
//        assertThat(updatedCart.getTotalPrice()).isEqualTo(game2.getPrice());
//    }
//
//    @Test
//    @DisplayName("장바구니에 없는 게임은 제거할 수 없다.")
//    void removeNonExistentGameFromCartThrowsException() {
//        // Given
//        orderService.addGameToCart(testUserId, game1.getId()); // game1만 추가
//
//        // When & Then
//        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.removeGameFromCart(testUserId, game2.getId()));
//        assertEquals(ErrorCode.GAME_NOT_IN_CART, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("사용자의 모든 주문 목록을 정상적으로 반환한다.")
//    void getOrdersByUserIdReturnsCorrectOrders() {
//        // Given
//        // 장바구니 생성 (PENDING)
//        orderService.addGameToCart(testUserId, game1.getId());
//        Order cart = orderService.getOrCreateCart(testUserId);
//
//        // 완료된 주문 생성 (PAYMENT_COMPLETED)
//        Order completedOrder = orderRepository.save(new Order(testUserId));
//        completedOrder.setStatus(OrderStatus.PAYMENT_COMPLETED);
//        orderRepository.save(completedOrder);
//
//        // When
//        List<Order> userOrders = orderService.getOrdersByUserId(testUserId);
//
//        // Then
//        assertThat(userOrders).isNotNull();
//        assertThat(userOrders).hasSize(2); // PENDING과 PAYMENT_COMPLETED 주문 모두 포함
//        assertThat(userOrders).extracting(Order::getOrderId).contains(cart.getOrderId(), completedOrder.getOrderId());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 주문 ID 조회 시 예외가 발생한다.")
//    void findByNonExistentOrderIdThrowsException() {
//        // Given
//        Long nonExistentOrderId = 9999L;
//
//        // When & Then
//        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.findByOrderId(nonExistentOrderId));
//        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
//    }
//}
