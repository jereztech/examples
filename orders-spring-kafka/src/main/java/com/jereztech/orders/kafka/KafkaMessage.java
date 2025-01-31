package com.jereztech.orders.kafka;

import com.jereztech.orders.data.entities.Order;
import lombok.Builder;

@Builder
public record KafkaMessage(ActionType action, Order order) {
}


