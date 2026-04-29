package com.example.demo.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTopicConfigTest {

    @Test
    void demoTopic_shouldCreateTopicWithCorrectName() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        ReflectionTestUtils.setField(config, "topicName", "test-topic");

        NewTopic topic = config.demoTopic();

        assertThat(topic.name()).isEqualTo("test-topic");
    }

    @Test
    void demoTopic_shouldCreateTopicWith3Partitions() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        ReflectionTestUtils.setField(config, "topicName", "test-topic");

        NewTopic topic = config.demoTopic();

        assertThat(topic.numPartitions()).isEqualTo(3);
    }

    @Test
    void demoTopic_shouldCreateTopicWith1Replica() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        ReflectionTestUtils.setField(config, "topicName", "test-topic");

        NewTopic topic = config.demoTopic();

        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }
}
