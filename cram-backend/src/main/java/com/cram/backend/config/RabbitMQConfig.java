package com.cram.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter(mapper);

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public DirectExchange chatExchange() {
        return new DirectExchange("chat.direct");
    }

    @Bean
    public Queue groupChatQueue() {
        return new Queue("group.chat.db.queue");
    }

    @Bean
    public Binding groupChatBinding() {
        return BindingBuilder.bind(groupChatQueue())
                .to(chatExchange())
                .with("group.chat.message");
    }

    @Bean
    public Queue meetingRoomChatQueue() {
        return new Queue("meeting.room.chat.db.queue");
    }

    @Bean
    public Binding meetingRoomChatBinding() {
        return BindingBuilder.bind(meetingRoomChatQueue())
                .to(chatExchange())
                .with("meeting.room.chat.message");
    }

    @Bean
    public Queue meetingRoomInActiveQueue() {
        return new Queue("meeting.room.inactive.queue");
    }

    @Bean
    public Binding meetingRoomInActiveBinding() {
        return BindingBuilder.bind(meetingRoomInActiveQueue())
                .to(chatExchange())
                .with("meeting.room.removed");
    }

    @Bean
    public Queue meetingRoomActiveQueue() {
        return new Queue("meeting.room.active.queue");
    }

    @Bean
    public Binding meetingRoomActiveBinding() {
        return BindingBuilder.bind(meetingRoomActiveQueue())
                .to(chatExchange())
                .with("meeting.room.create");
    }
}
