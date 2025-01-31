package com.jereztech.orders.kafka.listeners;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.kafka.KafkaMessage;
import com.jereztech.orders.kafka.producers.OrderProcessingProducer;
import com.jereztech.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

// Topic reserved for integration with other microservices.
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {

    public static final String TOPIC = "orders";

    private final OrderService orderService;
    private final OrderProcessingProducer orderProcessingProducer;

    @KafkaListener(topics = TOPIC)
    public void listen(@Payload KafkaMessage kafkaMessage) {
        Order order = kafkaMessage.order();
        log.info("Received: KafkaMessage[action={}, order={}]", kafkaMessage.action(), order);
        switch (kafkaMessage.action()) {
            case RECEIVE_ORDER -> {
                Order received = orderService.save(order);
                orderProcessingProducer.send(received);
            }
            case CANCEL_ORDER -> orderService.cancel(order);
        }
    }

}
