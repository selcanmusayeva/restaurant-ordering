package com.ordering.restaurant.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String KITCHEN_ORDERS_QUEUE = "kitchen.orders";
    public static final String WAITER_NOTIFICATIONS_QUEUE = "waiter.notifications";
    public static final String EXCHANGE = "restaurant.exchange";

    @Bean
    public Queue kitchenOrdersQueue() {
        return new Queue(KITCHEN_ORDERS_QUEUE, true);
    }

    @Bean
    public Queue waiterNotificationsQueue() {
        return new Queue(WAITER_NOTIFICATIONS_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding kitchenOrdersBinding(Queue kitchenOrdersQueue, DirectExchange exchange) {
        return BindingBuilder.bind(kitchenOrdersQueue)
                .to(exchange)
                .with(KITCHEN_ORDERS_QUEUE);
    }

    @Bean
    public Binding waiterNotificationsBinding(Queue waiterNotificationsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(waiterNotificationsQueue)
                .to(exchange)
                .with(WAITER_NOTIFICATIONS_QUEUE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
} 