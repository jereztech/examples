package com.jereztech.orders.kafka.producers;

import com.jereztech.orders.data.entities.Order;

@FunctionalInterface
public interface IOrderProducer {
    void send(Order order);
}
