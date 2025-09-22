package com.imfine.ngs.support.repository;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.support.entity.OrderDetail;
import com.imfine.ngs.support.entity.OrderDetailEntity;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, OrderDetail> {
    Optional<OrderDetailEntity> findById_OrderId(Long idOrderId);
    List<OrderDetailEntity> findById_OrderIdIn(List<Long> orderId);
}
