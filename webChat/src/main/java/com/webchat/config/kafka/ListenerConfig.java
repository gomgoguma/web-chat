package com.webchat.config.kafka;

import com.webchat.msg.object.Msg;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class ListenerConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Msg> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Msg> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Msg> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(Msg.class));
    }

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.KAFKA_BROKER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 역직렬화
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // 역직렬화
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");    // earliest : 가장 이른 오프셋부터 모든 메시지를 읽는다.
                                                                            // latest : 가장 최근 오프셋부터 새로운 메시지를 읽는다.
        return config;
    }

}
