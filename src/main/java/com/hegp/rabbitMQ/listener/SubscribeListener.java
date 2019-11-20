package com.hegp.rabbitMQ.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hegp.po.Mail;

@Component
public class SubscribeListener {
	@RabbitListener(queues = "queue1")
	public void subscribe1(Mail mail) {
		System.out.println("订阅者1收到消息"+mail.toString());
	}

	@RabbitListener(queues = "queue2")
	public void subscribe2(Mail mail) {
		System.out.println("订阅者2收到消息"+mail.toString());
	}
}
