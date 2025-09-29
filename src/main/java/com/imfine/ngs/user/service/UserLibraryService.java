//package com.imfine.ngs.user.service;
//
//import com.imfine.ngs.game.entity.Game;
//import com.imfine.ngs.game.repository.GameRepository;
//import com.imfine.ngs.order.entity.Order;
//import com.imfine.ngs.order.entity.OrderStatus;
//import com.imfine.ngs.order.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class UserLibraryService {
//
//    private final OrderRepository orderRepository;
//    private final GameRepository gameRepository;
//
//    public List<Game> getUserLibrary(Long userId) {
//        List<Order> orders = orderRepository.findByUserId(userId);
//
//        // 대상 상태의 주문만 필터링 후, 게임 ID 집합 추출 (중복 제거)
//        Set<Long> gameIds = orders.stream()
//                .filter(o -> o.getStatus() == OrderStatus.PURCHASED_CONFIRMED
//                        || o.getStatus() == OrderStatus.REFUND_REQUESTED)
//                .flatMap(o -> o.getOrderDetails().stream())
//                .map(od -> od.getGame() != null ? od.getGame().getId() : null)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//
//        // 활성 게임만 반환
//        return gameIds.stream()
//                .map(gameRepository::findByIdAndIsActive)
//                .flatMap(Optional::stream)
//                .collect(Collectors.toList());
//    }
//}
//
