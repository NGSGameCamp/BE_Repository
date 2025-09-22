package com.imfine.ngs.support.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.support.entity.OrderDetail;
import com.imfine.ngs.support.entity.OrderDetailEntity;
import com.imfine.ngs.support.repository.OrderDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    // orderId가 하나 일 경우
    public OrderDetailEntity findGameByOrderId(long orderId) {
        System.out.println("test2: " + orderDetailRepository.findById_OrderId(orderId));
        return orderDetailRepository.findById_OrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Not Found OrderDetail : " + orderId));
    }

    // orderId가 여러개 일 경우
    public List<OrderDetailEntity> findByIdOrderIdIn(List<Long> orderIds) {
        if (orderIds.isEmpty()) {
            throw new IllegalArgumentException("Empty OrderIds");
        }
        System.out.println("test: " + orderDetailRepository.findById_OrderIdIn(orderIds));
        return orderDetailRepository.findById_OrderIdIn(orderIds);
    }

}
