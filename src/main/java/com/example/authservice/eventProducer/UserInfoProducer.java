package com.example.authservice.eventProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.example.authservice.model.UserDto;

@Service
public class UserInfoProducer {

    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;

    @Autowired
    public UserInfoProducer(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventToKafka(UserInfoEvent userDto) {
        Message<UserInfoEvent> message = MessageBuilder.withPayload(userDto)
                .setHeader(KafkaHeaders.TOPIC, "userDetails")
                .build();
        kafkaTemplate.send(message);
    }

}
