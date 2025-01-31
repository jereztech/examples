package com.jereztech.orders.data.repositories;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.data.entities.OrderItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {

    List<OrderItem> findByOrderRef(AggregateReference<Order, UUID> orderRef, Sort sort);

}
