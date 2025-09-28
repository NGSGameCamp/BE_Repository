package com.imfine.ngs.order.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import com.imfine.ngs.order.dto.CartDto;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/cart")
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Order cart = orderService.getOrCreateCart(principal.getUserId());
        return ResponseEntity.ok(new CartDto(cart));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<CartDto> addGameToCart(@RequestParam Long gameId, @AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Order updatedCart = orderService.addGameToCart(principal.getUserId(), gameId);
        return ResponseEntity.ok(new CartDto(updatedCart));
    }

    @DeleteMapping("/cart/remove/{gameId}")
    public ResponseEntity<CartDto> removeGameFromCart(@PathVariable Long gameId, @AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Order updatedCart = orderService.removeGameFromCart(principal.getUserId(), gameId);
        return ResponseEntity.ok(new CartDto(updatedCart));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        List<Order> orders = orderService.getOrdersByUserId(principal.getUserId());
        return ResponseEntity.ok(orders);
    }
}