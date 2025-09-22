package com.imfine.ngs.order.repository;

import com.imfine.ngs.order.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
}
