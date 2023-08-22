package com.webchat.config.kafka;

import com.webchat.msg.object.Msg;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class ProducerConfig {

    @Bean
    public ProducerFactory<String, Msg> producerFactory() {
        return new DefaultKafkaProducerFactory<>(ProducerConfig());
    }

    @Bean
    public Map<String, Object> ProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.KAFKA_BROKER); // Kafka 실행 주소
        config.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // String 직렬화
        config.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Json 직렬화
        return config;
    }

    @Bean
    public KafkaTemplate<String, Msg> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
}
