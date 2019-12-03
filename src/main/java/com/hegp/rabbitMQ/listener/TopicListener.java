package com.hegp.rabbitMQ.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hegp.config.RabbitMqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.hegp.po.Mail;

import java.io.IOException;
import java.util.Map;

@Component
public class TopicListener {
	@Autowired
	private ObjectMapper mapper;

	@RabbitListener(queues = "topicqueue1")
	public void displayTopic1(Message message, @Headers Map<String, Object> headers, Channel channel) {
		System.out.println("从topicqueue1取出消息"+toString());
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
		} finally {
			RabbitMqConfig.basicAck(channel, message, false);
		}
	}

	@RabbitListener(queues = "topicqueue2")
	public void displayTopic2(Message message, @Headers Map<String, Object> headers, Channel channel) {
		System.out.println("从topicqueue2取出消息");
		System.out.println(headers);
		System.out.println(message);
		try {
			Mail mail = mapper.readValue(message.getBody(), Mail.class);
			System.out.println("receiver success");
		} catch (IOException e) {
			e.printStackTrace();
			// 丢弃这条消息
			// channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
			// System.out.println("丢弃这条消息");
		} finally {
			RabbitMqConfig.basicAck(channel, message, false);
		}
	}

}
