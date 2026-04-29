package com.example.demo.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaTopicConfig — Configurazione dei topic Kafka.
 * Crea automaticamente all'avvio:
 * - demo-topic: topic principale
 * - demo-topic.DLT: Dead Letter Topic per i messaggi falliti
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${KAFKA_TOPIC}")
    private String topicName;

    /**
     * Topic principale dove vengono inviati i messaggi.
     */
    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Dead Letter Topic — riceve i messaggi che hanno fallito
     * tutti i tentativi di retry dal topic principale.
     * Il nome convenzionale è <topic-name>.DLT
     */
    @Bean
    public NewTopic demoTopicDlt() {
        return TopicBuilder.name(topicName + ".DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}