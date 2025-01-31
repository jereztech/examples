package com.jereztech.orders.kafka.producers;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.kafka.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.jereztech.orders.kafka.ActionType.PICKUP_ORDER;

// Topic reserved to notify consumers that the order is ready for pickup.
@Slf4j
@Component(OrderPickupProducer.TOPIC)
@RequiredArgsConstructor
public class OrderPickupProducer implements IOrderProducer {

    public static final String TOPIC = "orders-pickup";

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    @Async
    @Override
    public void send(Order order) {
        KafkaMessage kafkaMessage = KafkaMessage.builder()
                .action(PICKUP_ORDER)
                .order(order)
                .build();
        log.info("Sending: KafkaMessage[action={}, order={}]", kafkaMessage.action(), kafkaMessage.order());
        kafkaTemplate.send(TOPIC, kafkaMessage);
    }

}
