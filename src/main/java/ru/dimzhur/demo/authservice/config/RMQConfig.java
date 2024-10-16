package ru.dimzhur.demo.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Конфигурация для RabbitMQ
 */
@Configuration
public class RMQConfig {

    /**
     * Очередь для запросов на создание токенов
     */
    @Primary
    @Bean
    public Queue queueTokens() {
        return new Queue("auth.rpc.tokens");
    }

    /**
     * Очередь для запросов на валидацию пользовательских токенов
     */
    @Bean
    public Queue queueValidate() {
        return new Queue("auth.rpc.validate");
    }

    /**
     * Обменник
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("auth.rpc");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with("rpc");
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper jsonMapper) {
        return new Jackson2JsonMessageConverter(jsonMapper);
    }
}
