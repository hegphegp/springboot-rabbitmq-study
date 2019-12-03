package com.hegp.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RabbitMqConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    // 手动确认信息
    public static void basicAck(Channel channel, Message message, boolean multiple) {
        //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 手动确认信息
    public static void basicAck(Channel channel, long deliveryTag, boolean multiple) {
        //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 手动丢弃信息
    public static void basicNack(Channel channel, Message message, boolean multiple, boolean requeue) {
        try {
            //手动丢弃信息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, requeue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 手动丢弃信息
    public static void basicNack(Channel channel, long deliveryTag, boolean multiple, boolean requeue) {
        try {
            //手动丢弃信息
            channel.basicNack(deliveryTag, false, requeue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
