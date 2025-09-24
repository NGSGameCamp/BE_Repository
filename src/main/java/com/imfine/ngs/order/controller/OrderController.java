package com.imfine.ngs.order.controller;

import com.imfine.ngs.order.dto.response.CartResponseDto;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // TODO: 실제 userId는 SecurityContextHolder에서 가져오도록 수정 필요
    private Long getCurrentUserId() {
        return 1L; // 임시 사용자 ID
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponseDto> getCart() {
        Long userId = getCurrentUserId();
        Order cart = orderService.getOrCreateCart(userId);
        return ResponseEntity.ok(new CartResponseDto(cart));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<CartResponseDto> addGameToCart(@RequestParam Long gameId) {
        Long userId = getCurrentUserId();
        Order updatedCart = orderService.addGameToCart(userId, gameId);
        return ResponseEntity.ok(new CartResponseDto(updatedCart));
    }

    @DeleteMapping("/cart/remove/{gameId}")
    public ResponseEntity<CartResponseDto> removeGameFromCart(@PathVariable Long gameId) {
        Long userId = getCurrentUserId();
        Order updatedCart = orderService.removeGameFromCart(userId, gameId);
        return ResponseEntity.ok(new CartResponseDto(updatedCart));
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getMyOrders() {
        Long userId = getCurrentUserId();
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<CartResponseDto> dtoList = orders.stream()
                .map(CartResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}