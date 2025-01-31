package com.jereztech.orders.kafka;

import com.jereztech.orders.kafka.listeners.OrderListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Bean
    public KafkaAdmin.NewTopics ordersTopics() {
        return new KafkaAdmin.NewTopics(TopicBuilder.name(OrderListener.TOPIC).build());
    }

    @Bean
    public ConsumerFactory<String, KafkaMessage> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName(),
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaMessage.class.getName()
        ));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, KafkaMessage> producerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName()
        ));
    }

    @Bean
    public KafkaTemplate<String, KafkaMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
