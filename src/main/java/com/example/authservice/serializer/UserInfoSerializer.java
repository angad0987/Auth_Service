package com.example.authservice.serializer;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.authservice.eventProducer.UserInfoEvent;
import com.example.authservice.model.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    private ObjectMapper mapper;

    public UserInfoSerializer() {
        this.mapper = new ObjectMapper(); // Initialize the ObjectMapper
    }

    @Override
    public byte[] serialize(String key, UserInfoEvent userDto) {
        byte[] data = null;
        try {
            System.out.println(mapper.writeValueAsString(userDto));
            data = this.mapper.writeValueAsBytes(userDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
