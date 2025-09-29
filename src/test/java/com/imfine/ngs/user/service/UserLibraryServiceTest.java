//package com.imfine.ngs.user.service;
//
//import com.imfine.ngs.game.entity.Game;
//import com.imfine.ngs.game.repository.GameRepository;
//import com.imfine.ngs.order.entity.Order;
//import com.imfine.ngs.order.entity.OrderDetails;
//import com.imfine.ngs.order.entity.OrderStatus;
//import com.imfine.ngs.order.repository.OrderRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UserLibraryServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private GameRepository gameRepository;
//
//    @InjectMocks
//    private UserLibraryService userLibraryService;
//
//    @Test
//    @DisplayName("라이브러리 불러오기")
//    void getUserLibrary_filtersByStatus_andReturnsActiveDistinctGames() {
//        Long userId = 42L;
//
//        Game g1 = Game.builder().id(1L).name("G1").price(1000L).tag("t").isActive(true).build();
//        Game g2 = Game.builder().id(2L).name("G2").price(2000L).tag("t").isActive(true).build();
//        Game g3 = Game.builder().id(3L).name("G3").price(3000L).tag("t").isActive(false).build();
//
//        Order o1 = new Order(userId);
//        o1.setStatus(OrderStatus.PURCHASED_CONFIRMED);
//        o1.addOrderDetail(new OrderDetails(o1, g1));
//        o1.addOrderDetail(new OrderDetails(o1, g2));
//
//        Order o2 = new Order(userId);
//        o2.setStatus(OrderStatus.REFUND_REQUESTED);
//        o2.addOrderDetail(new OrderDetails(o2, g1));
//
//        Order o3 = new Order(userId);
//        o3.setStatus(OrderStatus.PAYMENT_COMPLETED);
//        o3.addOrderDetail(new OrderDetails(o3, g3));
//
//        when(orderRepository.findByUserId(userId)).thenReturn(List.of(o1, o2, o3));
//
//        when(gameRepository.findByIdAndIsActive(1L)).thenReturn(Optional.of(g1));
//        when(gameRepository.findByIdAndIsActive(2L)).thenReturn(Optional.of(g2));
//
//        List<Game> result = userLibraryService.getUserLibrary(userId);
//
//        assertThat(result).hasSize(2);
//        assertThat(result.stream().map(Game::getId).toList()).containsExactlyInAnyOrder(1L, 2L);
//    }
//}
