package com.jereztech.orders.services;

import com.jereztech.orders.data.entities.OrderItem;
import com.jereztech.orders.data.repositories.OrderItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderItemService extends BaseService<OrderItem, OrderItemRepository> {

    public OrderItemService(OrderItemRepository repository) {
        super(repository);
    }

    public List<OrderItem> findByOrderId(UUID orderId) {
        return repository.findByOrderRef(AggregateReference.to(orderId), Sort.by("index").ascending());
    }

}