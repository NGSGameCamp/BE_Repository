package com.imfine.ngs.support.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderDetails;
import com.imfine.ngs.order.repository.OrderDetailsRepository;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.support.dto.SupportRequestDto;
import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.entity.SupportCategory;
import com.imfine.ngs.support.repository.SupportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SupportService {

    private final SupportRepository supportRepository;
    private final OrderService orderService;
    private final OrderDetailsRepository orderDetailsRepository;

    public Support insertSupportRepo(long userId, SupportRequestDto requestDto, SupportCategory category) {

        return supportRepository.save(Support.builder()
                .userId(userId)
                .content(requestDto.getContent())
                .orderId(requestDto.getOrderId())
                .categoryId(category.getId())
                .createdAt(LocalDateTime.now())
                .build()
        );
    }

    public List<Support> findSupportByUserId(long userId) {
        return supportRepository.findAllByUserId(userId);
    }

    public List<Support> findSupportByCategoryId(long categoryId) {
        return supportRepository.findAllByCategoryId(categoryId);
    }

    public List<Support> findAllSupport() {
        return supportRepository.findAll();
    }

    public Support findById(long id) {
        return supportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id + ": 존재하지 않는 문의글입니다."));
    }

    // 정렬하는 메서드
    public List<Support> sortByDate(String sortBy, List<Support> supports) {

        Comparator<Support> comparator = switch (sortBy.toUpperCase()) {
            case "DESC" -> Comparator.comparing(Support::getCreatedAt).reversed();
            case "ASC" -> Comparator.comparing(Support::getCreatedAt);
            default -> throw new IllegalArgumentException("Invalid sort criteria: " + sortBy + ". Allowed values are: DESC, ASC");
        };

        return supports.stream().sorted(comparator).collect(Collectors.toList());
    }

    // 게임 목록 가져오기
    public List<Game> findGameByUserId(long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderDetails> orderDetails = orderDetailsRepository.findAllById(orders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        return orderDetails.stream().map(OrderDetails::getGame).collect(Collectors.toList());
    }
}
