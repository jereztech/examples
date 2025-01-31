package com.jereztech.orders.kafka.producers;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.kafka.KafkaMessage;
import com.jereztech.orders.kafka.listeners.OrderProcessingListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.jereztech.orders.kafka.ActionType.PROCESS_ORDER;

@Slf4j
@Component(OrderProcessingListener.TOPIC)
@RequiredArgsConstructor
public class OrderProcessingProducer implements IOrderProducer {

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    @Async
    @Override
    public void send(Order order) {
        KafkaMessage kafkaMessage = KafkaMessage.builder()
                .action(PROCESS_ORDER)
                .order(order)
                .build();
        log.info("Sending: KafkaMessage[action={}, order={}]", kafkaMessage.action(), kafkaMessage.order());
        kafkaTemplate.send(OrderProcessingListener.TOPIC, kafkaMessage);
    }

}
