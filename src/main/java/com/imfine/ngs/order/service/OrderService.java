package com.imfine.ngs.order.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderDetails;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderDetailsRepository;
import com.imfine.ngs.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final GameRepository gameRepository; // Game 조회를 위해 추가

    // 장바구니(PENDING 상태의 주문) 조회 또는 생성
    public Order getOrCreateCart(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING)
                .orElseGet(() -> orderRepository.save(new Order(userId)));
    }

    // 장바구니에 게임 추가
    public Order addGameToCart(Long userId, Long gameId) {
        Order cart = getOrCreateCart(userId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameId));

        // 이미 장바구니에 담겼는지 확인
        boolean isAlreadyInCart = cart.getOrderDetails().stream()
                .anyMatch(detail -> detail.getGame().equals(game));

        if (isAlreadyInCart) {
            throw new IllegalArgumentException("이미 장바구니에 담긴 게임입니다.");
        }

        OrderDetails orderDetails = new OrderDetails(cart, game);
        cart.addOrderDetail(orderDetails);

        return orderRepository.save(cart);
    }

    // 장바구니에서 게임 삭제
    public Order removeGameFromCart(Long userId, Long gameId) {
        Order cart = getOrCreateCart(userId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameId));

        OrderDetails detailToRemove = cart.getOrderDetails().stream()
                .filter(detail -> detail.getGame().equals(game))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 없는 게임입니다."));

        cart.getOrderDetails().remove(detailToRemove);
        orderDetailsRepository.delete(detailToRemove);

        return orderRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
