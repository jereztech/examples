package com.jereztech.orders.kafka.listeners;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.kafka.KafkaMessage;
import com.jereztech.orders.kafka.producers.OrderPickupProducer;
import com.jereztech.orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.jereztech.orders.kafka.ActionType.PROCESS_ORDER;

// Topic reserved for internal order processing.
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProcessingListener {

    public static final String TOPIC = "orders-processing";

    private final OrderService orderService;
    private final OrderPickupProducer orderPickupProducer;

    @KafkaListener(topics = TOPIC)
    public void listen(@Payload KafkaMessage kafkaMessage) {
        Assert.isTrue(PROCESS_ORDER.equals(kafkaMessage.action()), String.format("Only '%s' messages are allowed.", PROCESS_ORDER));
        Order order = kafkaMessage.order();
        log.info("Received: KafkaMessage[action={}, order={}]", kafkaMessage.action(), order);

        Order processed = orderService.process(order);
        log.info("Processed: Order[{}]", processed);

        orderPickupProducer.send(processed);
    }

}
