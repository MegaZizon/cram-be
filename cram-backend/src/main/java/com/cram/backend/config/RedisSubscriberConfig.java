//package com.cram.backend.config;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//
//@Configuration
//@RequiredArgsConstructor
//public class RedisSubscriberConfig {
//
//    private final RedisMessageListenerContainer container;
//    private final BackendListener backendListener;
//
//    @PostConstruct
//    public void addMessageListener() {
//        container.addMessageListener(backendListener, new ChannelTopic("save-group-chat"));
//        container.addMessageListener(backendListener, new ChannelTopic("save-meeting-room-chat"));
//    }
//}
