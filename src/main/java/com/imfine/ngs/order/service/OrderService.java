package com.imfine.ngs.order.service;

import com.imfine.ngs.order.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(String userId, List<Game> games) {
        if (games.isEmpty()) {
            throw new IllegalArgumentException("주문할 게임이 없습니다.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderItems(new ArrayList<>(games));
        order.setTotalPrice(calculateTotalPrice(games));
        order.setOrderStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    private long calculateTotalPrice(List<Game> games) {
        return games.stream()
                .mapToLong(Game::getPrice)
                .sum();
    }

    public void addGameToOrder(Order order, Game game) {
        if (order.getOrderItems().contains(game)) {
            throw new IllegalArgumentException("이미 주문에 담긴 게임입니다.");
        }
        order.getOrderItems().add(game);
        order.setTotalPrice(order.getTotalPrice() + game.getPrice());
        orderRepository.save(order);
    }

    public void removeGameFromOrder(Order order, Game game) {
        if (!order.getOrderItems().contains(game)) {
            throw new IllegalArgumentException("주문에 없는 게임입니다.");
        }
        order.getOrderItems().remove(game);
        order.setTotalPrice(order.getTotalPrice() - game.getPrice());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order findByOrderId(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
    }

    public void updateOrderStatus(long orderId, OrderStatus status) {
        Order order = findByOrderId(orderId);
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 테스트에서 사용하기 위한 헬퍼 메서드
    public void clearAllOrders() {
        orderRepository.deleteAll();
    }
}
