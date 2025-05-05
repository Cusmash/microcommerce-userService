package com.bread.userservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEmailNotification(String message) {
        kafkaTemplate.send("email-notification", message);
    }
}
