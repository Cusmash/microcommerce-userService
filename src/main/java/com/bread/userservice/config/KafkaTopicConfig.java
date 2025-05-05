package com.bread.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic emailNotificationTopic() {
        return new NewTopic("email-notification", 1, (short) 1);
    }

    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic("user-events", 1, (short) 1);
    }

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("user.created").partitions(1).replicas(1).build();
    }
}
