package com.hegp.rabbitMQ.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hegp.config.RabbitMqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.hegp.po.Mail;

import java.io.IOException;
import java.util.Map;

@Component
public class SubscribeListener {
	@Autowired
	private ObjectMapper mapper;

	@RabbitListener(queues = "queue1")
	public void subscribe1(Message message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, @Headers Map<String, Object> headers, Channel channel) {
		System.out.println("queue1队列监听器收到消息");
		System.out.println(headers);
		System.out.println(message);
		try {
			Mail mail = mapper.readValue(message.getBody(), Mail.class);
			System.out.println("receiver success");
		} catch (IOException e) {
			e.printStackTrace();
			// 丢弃这条消息
			// RabbitMqConfig.basicNack(channel, message, false, false);
			// System.out.println("丢弃这条消息");
			System.out.println("receiver fail");
		} finally {
			RabbitMqConfig.basicAck(channel, message, false);
		}
	}

	@RabbitListener(queues = "queue2")
	public void subscribe2(Message message, @Headers Map<String, Object> headers, Channel channel) {
		System.out.println("queue2队列监听器收到消息");
		System.out.println(headers);
		System.out.println(message);
		try {
			Mail mail = mapper.readValue(message.getBody(), Mail.class);
			System.out.println("receiver success");
		} catch (IOException e) {
			e.printStackTrace();
			// 丢弃这条消息
			// RabbitMqConfig.basicNack(channel, message, false, false);
			// System.out.println("丢弃这条消息");
			System.out.println("receiver fail");
		} finally {
			RabbitMqConfig.basicAck(channel, message, false);
		}
	}
}
